package jzam.arcedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jzam.arcedex.models.PokeResearch
import jzam.arcedex.models.PokeSort
import jzam.arcedex.models.Pokemon
import jzam.arcedex.models.ResearchProgress
import jzam.arcedex.ui.theme.ArcedexTheme
import jzam.arcedex.ui.theme.Typography
import jzam.arcedex.utils.*
import jzam.arcedex.viewmodels.PokeResearchViewModel
import jzam.arcedex.viewmodels.PokeResearchViewModelFactory

/*
 * Arcedex by jzam (https://github.com/jzam)
 *
 * This is the main activity of the Arcedex app. It displays a sortable and searchable list of
 * Pokemon. Each Pokemon can be clicked to display the research tasks for that Pokemon. Research
 * task progress can be tracked by clicking the box that matches the new progression. To clear
 * progression of a task, click the box matching the current progression. This screen also displays
 * the user's research rank progress based on all tasks completed.
 */
class MainActivity : ComponentActivity() {

    private val pokeResearchViewModel: PokeResearchViewModel by viewModels {
        PokeResearchViewModelFactory((application as ArcedexApplication).pokeResearchRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArcedexTheme {
                ArcedexApp(pokeResearchViewModel)
            }
        }
    }
}

//Top-level screen composable that keeps track of various state variables used across the screen
//and passes them along to the child composables that need them
@Composable
fun ArcedexApp(pokeResearchViewModel: PokeResearchViewModel) {
    val researchTasks by pokeResearchViewModel.researchTasks.observeAsState()
    val pokedex by pokeResearchViewModel.pokedex.observeAsState()
    val researchProgress by pokeResearchViewModel.researchProgress.observeAsState()
    val pokeSort by pokeResearchViewModel.pokesort.observeAsState()
    val inSearchMode by pokeResearchViewModel.inSearchMode.observeAsState()
    val searchedText by pokeResearchViewModel.searchedText.observeAsState()
    val userPoints by pokeResearchViewModel.userPoints.observeAsState()
    val pokemonToResearchTasks by pokeResearchViewModel.pokemonToResearchTasks.observeAsState()


    //Recalculate progress on recomposition, need to make sure research tasks has been fetched
    //before running or this will fail
    if (researchTasks != null) {
        pokeResearchViewModel.calcProgress()
    }

    //Show the main app screen or the initialization screen if we are still waiting on the view
    //model to retrieve all tasks from the repository
    if (researchTasks == null || researchTasks!!.size < 242) {
        InitializationScreen()
    } else {
        Scaffold(
            topBar = {
                ArcedexTopBar(
                    userPoints = userPoints!!,
                    onSortChosen = pokeResearchViewModel::setSort
                )
            },
            bottomBar = {
                ArcedexBottomBar(
                    inSearchMode = inSearchMode!!,
                    searchText = searchedText!!,
                    onSearch = pokeResearchViewModel::searchPokedex,
                    searchClear = pokeResearchViewModel::searchClear
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Pokedex(
                    pokedex = pokedex!!,
                    progress = researchProgress!!,
                    pokeSort = pokeSort!!,
                    onGoalClick = pokeResearchViewModel::onGoalClick,
                    pokemonToResearchTasks = pokemonToResearchTasks
                )
            }
        }
    }
}

//App's top bar - Shows app name, research rank progress, and Sort button
@Composable
fun ArcedexTopBar(userPoints: Int, onSortChosen: (PokeSort) -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            ResearchRankInfo(userPoints)
            SortButton(onSortChosen)
        }
    )
}

//Display current research rank, progress bar to next rank, and points to next rank
@Composable
fun ResearchRankInfo(userPoints: Int) {
    val researchRank = getResearchRank(userPoints)
    val pointsToNext = getPointsToNextRankText(userPoints)
    val barProgress = getRankProgress(userPoints.toFloat())

    Column {
        Text(researchRank, style = Typography.button)
        LinearProgressIndicator(
            progress = barProgress,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .width(150.dp)
                .padding(8.dp)
        )
        Text(pointsToNext, style = Typography.caption)
    }
}

//Button to set how the Pokemon list is sorted
@Composable
fun SortButton(onSortChosen: (PokeSort) -> Unit) {
    var sortExpanded by remember { mutableStateOf(false) }

    Column {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .border(1.dp, Color.White)
                .clickable { sortExpanded = true }
        ) {
            Text(
                stringResource(R.string.sort_label),
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .background(MaterialTheme.colors.primary)
                    .padding(all = 8.dp)
            )
        }
        DropdownMenu(
            expanded = sortExpanded,
            onDismissRequest = { sortExpanded = false }
        ) {
            DropdownMenuItem(onClick = { onSortChosen(PokeSort.HISUI) }) {
                Text(stringResource(R.string.hisui_sort_label))
            }
            DropdownMenuItem(onClick = { onSortChosen(PokeSort.ALPHABETICAL) }) {
                Text(stringResource(R.string.alpha_sort_label))
            }
            DropdownMenuItem(onClick = { onSortChosen(PokeSort.NATIONAL) }) {
                Text(stringResource(R.string.national_sort_label))
            }
        }
    }
}

//Pokedex screen - list Pokemon and click a Pokemon to expand and see their research tasks
@Composable
fun Pokedex(
    pokedex: List<Pokemon>, progress: List<ResearchProgress>,
    pokeSort: PokeSort, onGoalClick: (PokeResearch, Int) -> Unit,
    pokemonToResearchTasks: Map<String, MutableList<PokeResearch>>?
) {
    if (pokedex.isNotEmpty()) {
        LazyColumn {
            items(pokedex) {
                PokedexPokemon(
                    pokemon = it,
                    tasks = pokemonToResearchTasks?.get(it.name),
                    progress = progress,
                    pokeSort = pokeSort,
                    onGoalClick = onGoalClick
                )
            }
        }
    } else {
        ShowEmptySearch()
    }
}

//Display for a Pokemon from the Pokedex screen
@Composable
fun PokedexPokemon(
    pokemon: Pokemon, tasks: MutableList<PokeResearch>?, progress: List<ResearchProgress>,
    pokeSort: PokeSort, onGoalClick: (PokeResearch, Int) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        Column {
            PokemonHeaderRow(
                pokemon = pokemon,
                progress = progress,
                pokeSort = pokeSort,
                isExpanded = isExpanded,
                onClick = { isExpanded = !isExpanded })
            if (isExpanded) {
                if (tasks != null) {
                    for (item in tasks) {
                        TaskRow(item, onGoalClick)
                    }
                }
            }
        }
    }
}

//Header row that displays info about Pokemon and a summary of its research progress.
@Composable
fun PokemonHeaderRow(
    pokemon: Pokemon, progress: List<ResearchProgress>, pokeSort: PokeSort, isExpanded: Boolean,
    onClick: () -> Unit
) {
    val pokeProgress = progress.find { it.name == pokemon.name }
    val bgColor: Color

    if (isExpanded) {
        bgColor = MaterialTheme.colors.secondaryVariant
    } else {
        bgColor = MaterialTheme.colors.background
    }

    if (pokeProgress != null) {
        Row(modifier = Modifier
            .background(MaterialTheme.colors.secondary)
            .fillMaxWidth()
            .clickable { onClick() }) {
            PokemonImage(
                pokemon.imgId, 50, stringResource(id = R.string.pokemon_pic_desc), bgColor
            )
            PokemonProgress(
                modifier = Modifier.weight(1f),
                pokemon = pokemon,
                pokeProgress = pokeProgress,
                pokeSort = pokeSort
            )
            ProgressPokeballImage(pokeProgress)
        }
    }
}

//Generic image format for this screen
@Composable
fun PokemonImage(imgId: Int, size: Int, desc: String, color: Color) {
    Image(
        painter = painterResource(id = imgId),
        contentDescription = desc,
        modifier = Modifier
            .size(size.dp)
            .clip(RectangleShape)
            .border(1.5.dp, MaterialTheme.colors.secondary, RectangleShape)
            .background(color)
    )
}

//Display Pokemon's basic info and research progress summary
@Composable
fun PokemonProgress(
    modifier: Modifier, pokemon: Pokemon, pokeProgress: ResearchProgress,
    pokeSort: PokeSort
) {
    Column(modifier = modifier) {
        Row {
            if (pokeSort == PokeSort.NATIONAL) {
                Text(
                    formatPokemonId(pokemon.natId), color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else if (pokeSort == PokeSort.HISUI) {
                Text(
                    formatPokemonId(pokemon.hisuiId), color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            Text(
                pokemon.name, color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Text(
            formatPokemonResearchInfo(
                goalsDone = pokeProgress.goalsDone,
                goalsTotal = pokeProgress.goalsTotal,
                pointsDone = pokeProgress.pointsDone + pokeProgress.bonusEarned,
                pointsTotal = pokeProgress.pointsTotal
            ),
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

//Show Pokeball image if enough research points have been earned for a Pokemon.
@Composable
fun ProgressPokeballImage(pokeProgress: ResearchProgress) {
    val pokeballImg: Int
    if (pokeProgress.pointsDone >= 100) {
        if (pokeProgress.pointsDone + pokeProgress.bonusEarned == pokeProgress.pointsTotal) {
            pokeballImg = R.drawable.masterball
        } else {
            pokeballImg = R.drawable.pokeball
        }
        PokemonImage(
            imgId = pokeballImg, size = 50,
            desc = stringResource(R.string.pokeball_pic_desc),
            color = MaterialTheme.colors.background
        )
    }
}

//Show a research task with points icon, description, and clickable progress goals
@Composable
fun TaskRow(pokemonTask: PokeResearch, onGoalClick: (PokeResearch, Int) -> Unit) {

    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PointsIcon(points = pokemonTask.points)
        Text(
            pokemonTask.task, modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
        GoalText(goal = pokemonTask.goal1, goalNum = 1, pokemonTask, onGoalClick)
        GoalText(goal = pokemonTask.goal2, goalNum = 2, pokemonTask, onGoalClick)
        GoalText(goal = pokemonTask.goal3, goalNum = 3, pokemonTask, onGoalClick)
        GoalText(goal = pokemonTask.goal4, goalNum = 4, pokemonTask, onGoalClick)
        GoalText(goal = pokemonTask.goal5, goalNum = 5, pokemonTask, onGoalClick)
    }
}

//Points icon. 20 = double points, 10 = standard points
@Composable
fun PointsIcon(points: Int) {
    val color: Color
    if (points == 20) {
        color = MaterialTheme.colors.primary
    } else {
        color = Color.White
    }
    PokemonImage(
        imgId = R.drawable.double_points, size = 25,
        desc = stringResource(id = R.string.points_icon_desc), color = color
    )
}

//Goal description that can clicked to modify goal progress for a research task
@Composable
fun GoalText(
    goal: String, goalNum: Int, pokemonTask: PokeResearch,
    onGoalClick: (PokeResearch, Int) -> Unit
) {
    var backgroundColor = MaterialTheme.colors.background

    if (pokemonTask.goalProgress >= goalNum) backgroundColor = MaterialTheme.colors.secondaryVariant
    if (goal.isNotBlank()) {
        Card(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colors.secondary)
                .clickable { onGoalClick(pokemonTask, goalNum) }
        ) {
            Text(
                goal, modifier = Modifier
                    .background(backgroundColor)
                    .padding(all = 8.dp)
            )
        }
    }
}

//Screen that displays when search results in an empty list
@Composable
fun ShowEmptySearch() {
    val emptyPokemon = jzam.arcedex.data.Pokedex.emptyDex
    val emptyResearch =
        listOf(ResearchProgress(name = emptyPokemon.name, goalsTotal = 0, pointsTotal = 0))
    Column(Modifier.padding(8.dp)) {
        PokemonHeaderRow(pokemon = emptyPokemon, progress = emptyResearch,
            pokeSort = PokeSort.HISUI, isExpanded = false, onClick = {})
        Text(text = stringResource(R.string.red_quote), fontSize = 50.sp)
        Text(text = stringResource(R.string.red_quote), fontSize = 50.sp)
        Text(text = stringResource(R.string.search_fail_message))
    }
}

//App's bottom bar - used for searching the Pokemon list
@Composable
fun ArcedexBottomBar(
    inSearchMode: Boolean, searchText: String, onSearch: (String) -> Unit,
    searchClear: () -> Unit
) {
    BottomAppBar {
        if (!inSearchMode) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    formatSearchedText(searchText), color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .padding(all = 4.dp)
                )
            }
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color.White)
                    .clickable { searchClear() }
            ) {
                Text(
                    stringResource(R.string.clear_label),
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .padding(all = 4.dp)
                )
            }
        } else {
            SearchInputText(onSearch)
        }
    }
}

//Search bar and button
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.SearchInputText(
    onDone: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = text,
        onValueChange = { text = it },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDone(text)
            keyboardController?.hide()
        }),
        modifier = Modifier
            .border(4.dp, MaterialTheme.colors.primary)
            .weight(1f),
        label = { Text(stringResource(R.string.search_label)) }
    )
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .border(1.dp, Color.White)
            .clickable { onDone(text) }
    ) {
        Text(
            stringResource(R.string.done_label),
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .padding(all = 4.dp)
        )
    }
}

//Temporary screen when ViewModel data is not ready to display yet. Probably replace with splash
//screen when I get a chance.
@Composable
fun InitializationScreen() {
    Column(Modifier.padding(8.dp)) {
        PokemonImage(
            imgId = R.drawable.sprite79, size = 200,
            desc = stringResource(id = R.string.waiting_pic_desc),
            color = MaterialTheme.colors.background
        )
        Text(stringResource(R.string.init_message1), style = Typography.h5)
        Text(stringResource(R.string.init_message2), style = Typography.h6)
    }
}
