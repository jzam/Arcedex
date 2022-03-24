package jzam.arcedex.viewmodels

import androidx.lifecycle.*
import jzam.arcedex.data.PokeResearchRepository
import jzam.arcedex.data.Pokedex
import jzam.arcedex.models.*
import jzam.arcedex.utils.translate
import kotlinx.coroutines.launch

/*
 * ViewModel for the main (and currently the only) Arcedex screen
 */
class PokeResearchViewModel(
    private val repository: PokeResearchRepository
) : ViewModel() {

    //List of all Pokemon research tasks
    private val _researchTasks: MutableLiveData<List<PokeResearch>> = MutableLiveData(listOf())
    var researchTasks: LiveData<List<PokeResearch>> = _researchTasks

    //List of all Pokemon
    private val _pokedex: MutableLiveData<List<Pokemon>> = MutableLiveData((listOf()))
    var pokedex: LiveData<List<Pokemon>> = _pokedex

    //List of objects that track how much research progress has been completed for each Pokemon
    private val _researchProgress: MutableLiveData<List<ResearchProgress>> =
        MutableLiveData(listOf())
    var researchProgress: LiveData<List<ResearchProgress>> = _researchProgress

    //The current sort of this screen
    private val _pokesort: MutableLiveData<PokeSort> = MutableLiveData()
    val pokesort: LiveData<PokeSort> = _pokesort

    //Is the search box ready to receive input?
    private val _inSearchMode: MutableLiveData<Boolean> = MutableLiveData(false)
    var inSearchMode: LiveData<Boolean> = _inSearchMode

    //The text that has been searched for
    private val _searchedText: MutableLiveData<String> = MutableLiveData()
    var searchedText: LiveData<String> = _searchedText

    //Sum of all research points to calculate the user's research rank
    private val _userPoints: MutableLiveData<Int> = MutableLiveData()
    var userPoints: LiveData<Int> = _userPoints

    //Map of a Pokemon name to the list of research tasks associated to it for quicker processing
    private val _pokemonToResearchTasks: MutableLiveData<Map<String, MutableList<PokeResearch>>> =
        MutableLiveData()
    var pokemonToResearchTasks: LiveData<Map<String, MutableList<PokeResearch>>> =
        _pokemonToResearchTasks

    private var language = SupportedLanguage.ENGLISH

    //Set initial values on ViewModel initialization
    init {
        researchTasks = repository.getResearchTasks()
        _pokesort.value = PokeSort.HISUI
        _pokedex.value = Pokedex.pokedex.sortedBy { it.hisuiId }
        _searchedText.value = ""
        _inSearchMode.value = true
    }

    //Calculates research progress for each Pokemon and builds map of Pokemon name to their research
    //tasks.
    fun calcProgress() {
        var idx = 0
        var points = 0
        val progList: MutableList<ResearchProgress> = mutableListOf()
        val mapPokemonToResearchTasks: MutableMap<String, MutableList<PokeResearch>> =
            mutableMapOf()
        val nameToProgListIdx: MutableMap<String, Int> = mutableMapOf()
        val taskList = researchTasks.value
        if (taskList != null) {
            for (task in taskList) {
                if (nameToProgListIdx.containsKey(task.name)) {
                    val tempIdx = nameToProgListIdx.getValue(task.name)
                    progList[tempIdx].goalsDone += task.goalProgress
                    progList[tempIdx].goalsTotal += task.totalGoals
                    progList[tempIdx].pointsDone += task.goalProgress * task.points
                    progList[tempIdx].pointsTotal += task.totalGoals * task.points
                    if (progList[tempIdx].pointsDone >= 100) {
                        progList[tempIdx].bonusEarned = 100
                    } else {
                        progList[tempIdx].bonusEarned = 0
                    }
                } else {
                    val newProgress = ResearchProgress(
                        name = task.name,
                        goalsDone = task.goalProgress,
                        goalsTotal = task.totalGoals,
                        pointsDone = task.goalProgress * task.points,
                        //TODO - Adding bonus points here, make this a constant
                        pointsTotal = task.totalGoals * task.points + 100
                    )
                    if (newProgress.pointsDone >= 100) {
                        newProgress.bonusEarned = 100
                    } else {
                        newProgress.bonusEarned = 0
                    }
                    progList.add(idx, newProgress)
                    nameToProgListIdx[task.name] = idx
                    idx += 1
                }
                if (mapPokemonToResearchTasks.contains(task.name)) {
                    mapPokemonToResearchTasks.getValue(task.name).add(task)
                } else {
                    mapPokemonToResearchTasks[task.name] = mutableListOf(task)
                }
            }
        }
        for (item in progList) {
            points += item.pointsDone + item.bonusEarned
        }
        _researchProgress.value = progList
        _pokemonToResearchTasks.value = mapPokemonToResearchTasks
        _userPoints.value = points
    }

    //Set and execute the sorting.
    fun setSort(sort: PokeSort) {
        _pokesort.value = sort

        when (sort) {
            PokeSort.ALPHABETICAL -> {
                _pokedex.value = pokedex.value?.sortedBy { translate(language, it.name) }
            }
            PokeSort.NATIONAL -> {
                _pokedex.value = pokedex.value?.sortedBy { it.natId }
            }
            else -> {
                _pokedex.value = pokedex.value?.sortedBy { it.hisuiId }
            }
        }
    }

    //Search for given text in research task list. Can match on Pokemon name or description of a
    //task. Set Pokemon list to filtered list.
    fun searchPokedex(searchText: String) {
        var idx = 0
        val matchingPokemon: MutableList<Pokemon> = mutableListOf()
        val nameToListIdx: MutableMap<String, Int> = mutableMapOf()
        val taskList = researchTasks.value
        val oldPokedex = pokedex.value
        var translatedName: String
        var translatedTask: String
        val findText = translate(language, searchText).lowercase()
        if (taskList != null && oldPokedex != null) {
            for (task in taskList) {
                translatedName = translate(language, task.name).lowercase()
                translatedTask = translate(language, task.task).lowercase()
                if (!nameToListIdx.containsKey(task.name) &&
                    (translatedName.contains(findText) ||
                            translatedTask.contains(findText))
                ) {
                    for (pokemon in oldPokedex) {
                        if (pokemon.name == task.name) {
                            matchingPokemon.add(idx, pokemon)
                            nameToListIdx[task.name] = idx
                            idx += 1
                            break
                        }
                    }
                }
            }
        }
        _pokedex.value = matchingPokemon
        _inSearchMode.value = false
        _searchedText.value = searchText
        setSort(pokesort.value!!)
    }

    //Reset the search variables
    fun searchClear() {

        when (_pokesort.value) {
            PokeSort.ALPHABETICAL -> {
                _pokedex.value = Pokedex.pokedex.sortedBy { translate(language, it.name) }
            }
            PokeSort.NATIONAL -> {
                _pokedex.value = Pokedex.pokedex.sortedBy { it.natId }
            }
            else -> {
                _pokedex.value = Pokedex.pokedex.sortedBy { it.hisuiId }
            }
        }
        _searchedText.value = ""
        _inSearchMode.value = true
    }

    //Set the goal progress for a task based on what the user clicked. If they clicked the current
    //goal progress, reset progress to 0. Updating the task does not trigger recomposition, so
    //making a copy with changed values, deleting the original, and inserting copy instead.
    fun onGoalClick(task: PokeResearch, goalNum: Int) {
        viewModelScope.launch {
            val savTask = task.copy()
            if (goalNum == task.goalProgress) {
                savTask.goalProgress = 0
            } else {
                savTask.goalProgress = goalNum
            }
            repository.delete(task)
            repository.insert(savTask)
        }
    }

    fun setLanguage(lang: SupportedLanguage) {
        language = lang
    }
}

//Boilerplate view model factory code
class PokeResearchViewModelFactory(
    private val repository: PokeResearchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokeResearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokeResearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}