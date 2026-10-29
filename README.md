# Nightwatch 🐺

> A spatial, low-light Werewolf & Mafia moderator companion for Android. Freeform table layouts, tactile tracking, and zero paper.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20(SDK%2026+)-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://android.com)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20(M3E)-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

---

## What is Nightwatch?

Moderating Werewolf or Mafia with paper or simple list apps falls apart in low light and ignores who is actually sitting where.

**Nightwatch** matches the physical geometry of your room—whether players are seated around an irregular dining table, in a classic circle, or on floating chairs.

### Core Goals
- **Spatial Seating Canvas:** Map players to perimeter edges (top, bottom, left, right, floating) so your screen mirrors the room.
- **Two Moderation Modes:**
  - **Notebook (Scratchpad):** Zero friction, no enforced rules. Tap status flags (alive, marked, protected, silenced) and drive the narrative manually.
  - **Director (Engine):** Turn-by-turn night role queue with automatic dawn reconciliation and cascade resolution (e.g., Lovers).
- **Dark-Room First:** Minimal luminance and true blacks so the screen never casts light on your face during night phases.
- **Offline & Private:** 100% local, zero accounts, zero telemetry.

---

## Generative AI Experiment & Architecture

Nightwatch serves as a real-world testbed for building complete, non-trivial software exclusively through frontier generative AI models:

- **Architect & Orchestrator:** Powered by Gemini (with models like Gemini 3.6 Flash / Argon) driving high-level system architecture, spatial math breakdowns, domain boundaries, and step-by-step phased instructions.
- **Implementation Agents:** Autonomous coding agents with direct codebase access that consume these specifications to implement pure Kotlin state engines, custom Compose layouts, and test suites.

Every architectural decision, data model, and line of code is structured and prompted through this multi-tier model setup to test autonomous code generation limits on complex UI and game-loop states.

---

## Status

Active development. Building out the core domain logic and perimeter geometry before wiring up the Compose UI.

---

## License

Licensed under the [GNU General Public License v3.0](LICENSE).
