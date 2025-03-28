data class City(
    val cityName : String
){
    public fun doesMatchSearchQuery(query : String): Boolean {
        val normalizedQuery = query.trim().lowercase().removeDiacritics()
        val cityNameNormalized = cityName.trim().lowercase().removeDiacritics()
        return cityNameNormalized.startsWith(normalizedQuery);
    }
}

fun String.removeDiacritics(): String {
    return this.replace("[áàãâä]".toRegex(), "a")
        .replace("[éèêë]".toRegex(), "e")
        .replace("[íìîï]".toRegex(), "i")
        .replace("[óòõôö]".toRegex(), "o")
        .replace("[úùûü]".toRegex(), "u")
        .replace("[ç]".toRegex(), "c")
}
