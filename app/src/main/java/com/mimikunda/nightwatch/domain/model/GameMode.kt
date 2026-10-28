package com.mimikunda.nightwatch.domain.model

enum class GameMode {
    /**
     * Manual Scratchpad: Moderator manually toggles status flags at will.
     * No strict phase gating or automated validation.
     */
    NOTEBOOK,

    /**
     * Assisted State Machine: Automated turn-by-turn night role dispatch
     * with pure dawn casualty reconciliation.
     */
    DIRECTOR
}
