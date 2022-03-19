package jzam.arcedex.models

//Pokemon class with basic information unique to a Pokemon
data class Pokemon(
    val hisuiId: Int,
    val natId: Int,
    val name: String,
    val imgId: Int
)

//Enums representing sort options
enum class PokeSort {
    NATIONAL, ALPHABETICAL, HISUI
}
