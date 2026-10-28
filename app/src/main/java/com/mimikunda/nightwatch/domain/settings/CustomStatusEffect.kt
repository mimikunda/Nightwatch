package com.mimikunda.nightwatch.domain.settings

data class CustomStatusEffect(
    val id: String,
    val name: String,
    val colorArgb: Long,
    val iconKey: String,
    val isEnabled: Boolean = true,
    val isCustom: Boolean = false
) {
    companion object {
        val WOLF_TARGET = CustomStatusEffect("WOLF_TARGET", "Wolf Target", 0xFFFF5449, "CrisisAlert")
        val PROTECTED = CustomStatusEffect("PROTECTED", "Protected", 0xFF4FC3F7, "Shield")
        val SAVED_POTION = CustomStatusEffect("SAVED_POTION", "Saved Potion", 0xFFFFD54F, "Favorite")
        val POISONED = CustomStatusEffect("POISONED", "Poisoned", 0xFFD05CE3, "Warning")
        val SEER_INSPECTED = CustomStatusEffect("SEER_INSPECTED", "Seer Inspected", 0xFF9FA8DA, "Visibility")
        val MAYOR = CustomStatusEffect("MAYOR", "Mayor", 0xFFFFB300, "Star")
        val SILENCED = CustomStatusEffect("SILENCED", "Silenced", 0xFF8E8E93, "MicOff")

        val DEFAULT_EFFECTS = listOf(
            WOLF_TARGET, PROTECTED, SAVED_POTION, POISONED, SEER_INSPECTED, MAYOR, SILENCED
        )
    }
}
