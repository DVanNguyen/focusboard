# 🚀 FocusBoard (v4.0)

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room_Database-FF5722?style=for-the-badge&logo=sqlite&logoColor=white)
![AI Built](https://img.shields.io/badge/Built_with-AI_Agents-8A2BE2?style=for-the-badge)

**FocusBoard** is a unified, offline-first productivity workspace for Android. It eliminates context-switching fatigue by consolidating task management, block-based note-taking, and real-time team collaboration into a single, cohesive, highly responsive application.

## ✨ Key Features

* 📅 **Todoist-style Schedule:** A highly interactive absolute-positioned grid for time-blocking and event management.
* 📝 **Notion-style Block Editor:** A robust text editor supporting rich formatting, checkboxes, and `/` slash commands (H1, H2, tasks, quotes, topics) powered by Jetpack Compose.
* 👥 **Team Workspaces & Chat:** Dedicated spaces for teams with real-time chat, file sharing (with relative time formatting), and member management.
* ⚡ **Offline-First Architecture:** Zero-latency UI updates. All actions are immediately written to the local SQLite database via Coroutines/Flow, with background synchronization.

## 🛠️ Tech Stack & Architecture

* **UI Toolkit:** 100% Declarative UI with **Jetpack Compose** (Material3).
* **Architecture:** Clean Architecture + **MVVM** (Model-View-ViewModel).
* **Concurrency:** Kotlin Coroutines & `StateFlow`.
* **Local Database:** **Room Database** (v4) with complex relational entities and automated migrations.
* **Dependency Injection:** Dagger Hilt.

## 🤖 AI-Driven Development Workflow

This project heavily utilized advanced AI Agent workflows (Claude/Cursor) for rapid prototyping and complex refactoring:
1.  **Cross-Platform UI Translation:** Translated complex React/JSX web designs into pixel-perfect Jetpack Compose layouts using long-chain reasoning.
2.  **Architectural Refactoring:** Safely migrated a legacy XML/Fragment codebase to Jetpack Compose while strictly maintaining the underlying Offline-First Data Layer (Zero Logic Modification constraint).
3.  **Autonomous Debugging:** Resolved complex `IllegalStateException` Room database migration issues (v2 to v4) via context-aware schema analysis and fallback strategies.

---
*Built with ❤️ and AI.*
