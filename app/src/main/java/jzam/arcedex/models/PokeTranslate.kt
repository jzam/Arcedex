package jzam.arcedex.models

//Class for translation lookups
data class PokeTranslate(
    val oldText: String,
    val newText: String
)

//Enums representing supported languages
enum class SupportedLanguage {
    ENGLISH, JAPANESE
}
