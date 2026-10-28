package com.mimikunda.nightwatch.domain.model

data class RoleDeckConfig(
    val specialRoleCounts: Map<String, Int> = mapOf(
        "werewolf" to 2,
        "seer" to 1,
        "bodyguard" to 1,
        "witch" to 1
    )
) {
    /**
     * Generates a shuffled list of Role IDs of exact size [totalSeats].
     * Automatically fills remaining slots with "villager" (or truncates if special > totalSeats).
     */
    fun buildShuffledDeck(
        totalSeats: Int,
        random: kotlin.random.Random = kotlin.random.Random.Default
    ): List<String> {
        val specialList = specialRoleCounts.flatMap { (roleId, count) ->
            List(count.coerceAtLeast(0)) { roleId }
        }.take(totalSeats)

        val remainingVillagers = (totalSeats - specialList.size).coerceAtLeast(0)
        val fullDeck = specialList + List(remainingVillagers) { "villager" }
        return fullDeck.shuffled(random)
    }
}
