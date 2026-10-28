package com.mimikunda.nightwatch.domain.persistence

import android.content.Context
import com.mimikunda.nightwatch.domain.model.RoleTeam
import com.mimikunda.nightwatch.domain.model.Seat
import com.mimikunda.nightwatch.domain.model.SeatPosition
import com.mimikunda.nightwatch.domain.model.StatusMarker
import com.mimikunda.nightwatch.domain.model.TableEdge
import com.mimikunda.nightwatch.domain.settings.CustomRole
import com.mimikunda.nightwatch.domain.settings.CustomStatusEffect
import com.mimikunda.nightwatch.domain.settings.GameSettings
import com.mimikunda.nightwatch.domain.state.NotebookState

object NightwatchPersistence {

    private const val PREFS_NAME = "nightwatch_prefs"
    private const val KEY_SETTINGS = "key_game_settings"
    private const val KEY_NOTEBOOK_STATE = "key_notebook_state"

    fun saveSettings(context: Context, settings: GameSettings) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SETTINGS, settingsToJson(settings)).apply()
    }

    fun loadSettings(context: Context): GameSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_SETTINGS, null) ?: return GameSettings()
        return jsonToSettings(rawJson)
    }

    fun saveNotebookState(context: Context, state: NotebookState) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NOTEBOOK_STATE, notebookStateToJson(state)).apply()
    }

    fun loadNotebookState(context: Context): NotebookState? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_NOTEBOOK_STATE, null) ?: return null
        return jsonToNotebookState(rawJson)
    }

    fun clearSavedData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    // Pure Kotlin JSON Serialization Helpers (No Android stub dependencies)
    fun settingsToJson(settings: GameSettings): String {
        val rolesJson = settings.roles.joinToString(",") { role ->
            val effJson = if (role.associatedEffect != null) {
                """{"id":"${role.associatedEffect.id}","name":"${role.associatedEffect.name}","colorArgb":${role.associatedEffect.colorArgb},"iconKey":"${role.associatedEffect.iconKey}","isEnabled":${role.associatedEffect.isEnabled},"isCustom":${role.associatedEffect.isCustom}}"""
            } else "null"
            """{"roleId":"${role.roleId}","displayName":"${role.displayName}","team":"${role.team.name}","colorArgb":${role.colorArgb},"isEnabled":${role.isEnabled},"isCustom":${role.isCustom},"associatedEffect":$effJson}"""
        }

        val effectsJson = settings.effects.joinToString(",") { effect ->
            """{"id":"${effect.id}","name":"${effect.name}","colorArgb":${effect.colorArgb},"iconKey":"${effect.iconKey}","isEnabled":${effect.isEnabled},"isCustom":${effect.isCustom}}"""
        }

        return """{"roles":[$rolesJson],"effects":[$effectsJson],"tableShape":"${settings.tableShape.name}"}"""
    }

    fun jsonToSettings(rawJson: String): GameSettings {
        if (rawJson.isBlank()) return GameSettings()
        return try {
            val roleRegex = """"roleId":"([^"]+)","displayName":"([^"]+)","team":"([^"]+)","colorArgb":(-?\d+),"isEnabled":(true|false),"isCustom":(true|false)(?:,"associatedEffect":(null|\{.*?\}))?""".toRegex()
            val rolesList = mutableListOf<CustomRole>()

            roleRegex.findAll(rawJson).forEach { match ->
                val (roleId, displayName, teamName, colorStr, enabledStr, customStr, effJson) = match.destructured

                var associatedEffect: CustomStatusEffect? = null
                if (effJson != "null" && effJson.isNotBlank()) {
                    val effRegex = """"id":"([^"]+)","name":"([^"]+)","colorArgb":(-?\d+),"iconKey":"([^"]+)","isEnabled":(true|false),"isCustom":(true|false)""".toRegex()
                    val effMatch = effRegex.find(effJson)
                    if (effMatch != null) {
                        val (eId, eName, eColor, eIcon, eEnabled, eCustom) = effMatch.destructured
                        associatedEffect = CustomStatusEffect(
                            id = eId,
                            name = eName,
                            colorArgb = eColor.toLong(),
                            iconKey = eIcon,
                            isEnabled = eEnabled.toBoolean(),
                            isCustom = eCustom.toBoolean()
                        )
                    }
                }

                rolesList.add(
                    CustomRole(
                        roleId = roleId,
                        displayName = displayName,
                        team = RoleTeam.valueOf(teamName),
                        colorArgb = colorStr.toLong(),
                        isEnabled = enabledStr.toBoolean(),
                        isCustom = customStr.toBoolean(),
                        associatedEffect = associatedEffect
                    )
                )
            }

            val effectRegex = """"id":"([^"]+)","name":"([^"]+)","colorArgb":(-?\d+),"iconKey":"([^"]+)","isEnabled":(true|false),"isCustom":(true|false)""".toRegex()
            val effectsList = mutableListOf<CustomStatusEffect>()

            effectRegex.findAll(rawJson).forEach { match ->
                val (id, name, colorStr, iconKey, enabledStr, customStr) = match.destructured
                effectsList.add(
                    CustomStatusEffect(
                        id = id,
                        name = name,
                        colorArgb = colorStr.toLong(),
                        iconKey = iconKey,
                        isEnabled = enabledStr.toBoolean(),
                        isCustom = customStr.toBoolean()
                    )
                )
            }

            val shapeMatch = """"tableShape":"([^"]+)"""".toRegex().find(rawJson)
            val tableShape = shapeMatch?.groupValues?.get(1)?.let {
                try { com.mimikunda.nightwatch.domain.model.TableShape.valueOf(it) } catch (_: Exception) { null }
            } ?: com.mimikunda.nightwatch.domain.model.TableShape.RECTANGLE

            GameSettings(
                roles = if (rolesList.isNotEmpty()) rolesList else CustomRole.DEFAULT_ROLES,
                effects = if (effectsList.isNotEmpty()) effectsList else CustomStatusEffect.DEFAULT_EFFECTS,
                tableShape = tableShape
            )
        } catch (_: Exception) {
            GameSettings()
        }
    }

    fun notebookStateToJson(state: NotebookState): String {
        val seatsJson = state.seats.joinToString(",") { seat ->
            val nameVal = if (seat.name != null) "\"${seat.name}\"" else "null"
            val roleVal = if (seat.roleId != null) "\"${seat.roleId}\"" else "null"
            val normX = if (seat.position.customNormalizedX != null) "${seat.position.customNormalizedX}" else "null"
            val normY = if (seat.position.customNormalizedY != null) "${seat.position.customNormalizedY}" else "null"
            val activeArr = seat.activeStatuses.joinToString(",") { "\"${it.name}\"" }
            val customArr = seat.customStatusIds.joinToString(",") { "\"$it\"" }
            val linkedArr = seat.linkedSeatIds.joinToString(",") { "\"$it\"" }

            """{"id":"${seat.id}","index":${seat.index},"name":$nameVal,"roleId":$roleVal,"edge":"${seat.position.edge.name}","positionOnEdge":${seat.position.positionOnEdge},"normalizedOffset":${seat.position.normalizedOffset},"customNormalizedX":$normX,"customNormalizedY":$normY,"isAlive":${seat.isAlive},"activeStatuses":[$activeArr],"customStatusIds":[$customArr],"linkedSeatIds":[$linkedArr]}"""
        }
        return """{"seats":[$seatsJson]}"""
    }

    fun jsonToNotebookState(rawJson: String): NotebookState? {
        if (rawJson.isBlank()) return null
        return try {
            val seatRegex = """"id":"([^"]+)","index":(\d+),"name":(null|"([^"]+)"),"roleId":(null|"([^"]+)"),"edge":"([^"]+)","positionOnEdge":(\d+),"normalizedOffset":(-?\d+(?:\.\d+)?),"customNormalizedX":(null|-?\d+(?:\.\d+)?),"customNormalizedY":(null|-?\d+(?:\.\d+)?),"isAlive":(true|false),"activeStatuses":\[(.*?)],"customStatusIds":\[(.*?)],"linkedSeatIds":\[(.*?)]""".toRegex()
            val seatsList = mutableListOf<Seat>()

            seatRegex.findAll(rawJson).forEach { match ->
                val g = match.groupValues
                val id = g[1]
                val indexStr = g[2]
                val nameMatch = g[4]
                val roleMatch = g[6]
                val edgeName = g[7]
                val posEdgeStr = g[8]
                val normOffsetStr = g[9]
                val customXStr = g[10]
                val customYStr = g[11]
                val aliveStr = g[12]
                val activeStr = g[13]
                val customStr = g[14]
                val linkedStr = g[15]

                val activeSet = mutableSetOf<StatusMarker>()
                """"([^"]+)"""".toRegex().findAll(activeStr).forEach {
                    try {
                        activeSet.add(StatusMarker.valueOf(it.groupValues[1]))
                    } catch (_: Exception) {}
                }

                val customSet = mutableSetOf<String>()
                """"([^"]+)"""".toRegex().findAll(customStr).forEach {
                    customSet.add(it.groupValues[1])
                }

                val linkedSet = mutableSetOf<String>()
                """"([^"]+)"""".toRegex().findAll(linkedStr).forEach {
                    linkedSet.add(it.groupValues[1])
                }

                val normX = if (customXStr != "null") customXStr.toFloatOrNull() else null
                val normY = if (customYStr != "null") customYStr.toFloatOrNull() else null

                seatsList.add(
                    Seat(
                        id = id,
                        index = indexStr.toInt(),
                        name = if (nameMatch.isNotEmpty()) nameMatch else null,
                        roleId = if (roleMatch.isNotEmpty()) roleMatch else null,
                        position = SeatPosition(
                            edge = TableEdge.valueOf(edgeName),
                            positionOnEdge = posEdgeStr.toInt(),
                            normalizedOffset = normOffsetStr.toFloatOrNull() ?: 0f,
                            customNormalizedX = normX,
                            customNormalizedY = normY
                        ),
                        isAlive = aliveStr.toBoolean(),
                        activeStatuses = activeSet,
                        customStatusIds = customSet,
                        linkedSeatIds = linkedSet
                    )
                )
            }

            if (seatsList.isNotEmpty()) NotebookState(seats = seatsList) else null
        } catch (_: Exception) {
            null
        }
    }
}
