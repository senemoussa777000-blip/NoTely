# NOTELY — Plan de développement (à valider)

Nom : NOTELY — « Tes idées, toujours à portée de main. »
Catégorie : Productivité et organisation personnelle, Android native.
Date du diagnostic : 2026-11-26.

> Ce document est le plan à valider. Aucune installation, aucune modification de code n'a été faite. NOTA (Expo, existant) n'est pas touché. NOTELY sera un nouveau projet séparé.

## 1. Diagnostic de l'environnement

Machine : Linux x86_64, 4 CPU, 7,7 Go de RAM au total (519 Mo libres au moment du diagnostic, mémoire partagée), 149 Go de disque libre, Internet disponible.

### Disponible

| Outil | Version / état |
|---|---|
| Linux | Ubuntu (noyau 7.0.0-44-generic) |
| Node.js / npm | 24.22.1 / 10.9.4 |
| Git, curl, wget, unzip, Python 3 | installés |
| Internet vers Google | joignable, `commandlinetools-linux` répond 200 |
| Disque libre | 149 Go |

### Manquant (bloquant pour compiler)

| Outil | État |
|---|---|
| Java (JDK 17 requis par Gradle / AGP) | absent, `java` non trouvé, `JAVA_HOME` vide |
| Kotlin / kotlinc | absent |
| Gradle | absent |
| Android SDK (`ANDROID_HOME` / `ANDROID_SDK_ROOT`) | absent, aucun dossier `~/Android/Sdk` |
| Android Studio | absent (non indispensable, la compilation se fera en ligne de commande) |
| Émulateur Android / adb | absent |

### Conclusion du diagnostic

**On ne peut pas compiler d'APK aujourd'hui, sans rien installer.** C'est installable : JDK 17 + Android Command Line Tools + SDK. Première compilation Gradle lente (téléchargements d'environ 2 Go, 10 à 20 minutes), RAM à surveiller : Gradle sera limité à 3 Go de heap (`org.gradle.jvmargs=-Xmx3g`).

Pas d'émulateur ici. Les tests se feront avec l'APK debug installée sur un vrai téléphone Android (le tien).

## 2. Architecture technique proposée

Langage : Kotlin. UI : Jetpack Compose + Material 3. Navigation : Navigation Compose. Base locale : Room. Préférences : DataStore. Rappels : WorkManager. Asynchrone : Coroutines + Flow. Build : Gradle, Kotlin DSL.

Architecture MVVM, en couches :

- `ui/` : écrans Compose, ViewModels, navigation, thème. Aucun accès direct à la base.
- `domain/` : modèles métier, cas d'usage si besoin, règles (priorités, rappels, filtres).
- `data/` : Room (entités, DAO, base), repositories, DataStore, WorkManager.

Entités Room proposées :

- `Note` : id, titre, contenu (riche, en HTML/Markdown simple), couleur, catégorieId, épinglée, favorite, archivée, createdAt, updatedAt.
- `Task` : id, titre, description, terminée, priorité (basse / moyenne / haute), dateLimite, rappel, catégorieId, listeId, createdAt.
- `Event` : id, titre, description, début, fin, rappel, createdAt.
- `Category` : id, nom, couleur, type (note / tâche / événement).
- `TaskList` : id, nom (listes personnalisées de tâches).

Repositories : `NoteRepository`, `TaskRepository`, `EventRepository`, `SettingsRepository`. Chaque ViewModel expose des `StateFlow` depuis son repository. Aucune donnée fictive dans la version finale : base vide au premier lancement, avec un état vide élégant et un bouton pour créer le premier élément. Des données d'exemple ne seront utilisées que pendant les tests, puis supprimées.

Design system « Modern Creative » :

- Couleurs : Midnight #101827, Electric Blue #246BFD, Peach #FFB86B, Mint #9BD9C2, Cream #F5F1E8.
- Typographies : Plus Jakarta Sans (titres), Inter (textes), à intégrer dans `res/font` (téléchargées depuis Google Fonts, licence libre).
- Thèmes clair et sombre, cartes arrondies, espacements généreux, animations discrètes.
- Langue de départ : français. Structure prête pour d'autres langues.

Hors ligne d'abord : tout fonctionne sans Internet. Aucune clé API en dur. Architecture prête pour une synchronisation cloud ultérieure (un champ `updatedAt` + un identifiant stable par entité).

## 3. Arborescence complète du futur projet

```
notely/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/notely/app/
│       │   │   ├── MainActivity.kt
│       │   │   ├── NotelyApp.kt
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── NotelyDatabase.kt
│       │   │   │   │   ├── dao/
│       │   │   │   │   │   ├── NoteDao.kt
│       │   │   │   │   │   ├── TaskDao.kt
│       │   │   │   │   │   ├── EventDao.kt
│       │   │   │   │   │   └── CategoryDao.kt
│       │   │   │   │   └── entity/
│       │   │   │   │       ├── Note.kt
│       │   │   │   │       ├── Task.kt
│       │   │   │   │       ├── Event.kt
│       │   │   │   │       ├── Category.kt
│       │   │   │   │       └── TaskList.kt
│       │   │   │   ├── repository/
│       │   │   │   │   ├── NoteRepository.kt
│       │   │   │   │   ├── TaskRepository.kt
│       │   │   │   │   ├── EventRepository.kt
│       │   │   │   │   └── SettingsRepository.kt
│       │   │   │   ├── datastore/
│       │   │   │   │   └── SettingsDataStore.kt
│       │   │   │   └── worker/
│       │   │   │       ├── ReminderWorker.kt
│       │   │   │       └── NotificationHelper.kt
│       │   │   ├── domain/
│       │   │   │   ├── model/
│       │   │   │   │   ├── NoteUi.kt
│       │   │   │   │   ├── TaskUi.kt
│       │   │   │   │   └── EventUi.kt
│       │   │   │   └── util/
│       │   │   │       ├── DateUtils.kt
│       │   │   │       └── Priority.kt
│       │   │   └── ui/
│       │   │       ├── navigation/
│       │   │       │   ├── NavGraph.kt
│       │   │       │   └── Destinations.kt
│       │   │       ├── theme/
│       │   │       │   ├── Color.kt
│       │   │       │   ├── Type.kt
│       │   │       │   ├── Theme.kt
│       │   │       │   └── Shape.kt
│       │   │       ├── components/
│       │   │       │   ├── NotelyCard.kt
│       │   │       │   ├── EmptyState.kt
│       │   │       │   ├── SearchBar.kt
│       │   │       │   ├── ConfirmDialog.kt
│       │   │       │   └── ProgressBar.kt
│       │   │       ├── home/
│       │   │       │   ├── HomeScreen.kt
│       │   │       │   └── HomeViewModel.kt
│       │   │       ├── notes/
│       │   │       │   ├── NotesScreen.kt
│       │   │       │   ├── NoteEditorScreen.kt
│       │   │       │   └── NotesViewModel.kt
│       │   │       ├── tasks/
│       │   │       │   ├── TasksScreen.kt
│       │   │       │   └── TasksViewModel.kt
│       │   │       ├── calendar/
│       │   │       │   ├── CalendarScreen.kt
│       │   │       │   ├── EventEditorScreen.kt
│       │   │       │   └── CalendarViewModel.kt
│       │   │       └── profile/
│       │   │           ├── ProfileScreen.kt
│       │   │           └── ProfileViewModel.kt
│       │   ├── res/
│       │   │   ├── values/
│       │   │   │   ├── strings.xml
│       │   │   │   ├── themes.xml
│       │   │   │   └── colors.xml
│       │   │   ├── values-night/
│       │   │   │   └── themes.xml
│       │   │   ├── font/
│       │   │   │   ├── plus_jakarta_sans.ttf
│       │   │   │   └── inter.ttf
│       │   │   └── mipmap-xxxhdpi/
│       │   │       └── ic_launcher.png
│       │   └── androidTest/ + test/
│       │       ├── NoteDaoTest.kt
│       │       ├── TaskRepositoryTest.kt
│       │       └── NavigationTest.kt
│       ├── build.gradle.kts (module app)
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts (racine)
├── settings.gradle.kts
├── gradle.properties
├── local.properties (non versionné, contient le chemin du SDK)
├── README.md
└── docs/
    ├── ARCHITECTURE.md
    └── BUILD.md
```

Dépendances principales (versions à figer au moment de la création) : Compose BOM, Material 3, Navigation Compose, Room, DataStore Preferences, WorkManager, Lifecycle ViewModel Compose, Coroutines. Pas de dépendance inutile.

## 4. Plan de développement détaillé

### Phase 0 — Installation (après ta validation, environ 1 h)

1. Installer le JDK 17 (Temurin).
2. Télécharger Android Command Line Tools, installer dans `~/Android/Sdk/cmdline-tools/latest`.
3. Avec `sdkmanager` : `platform-tools`, `platforms;android-35`, `build-tools;35.0.0`.
4. Définir `ANDROID_HOME` et `JAVA_HOME`, vérifier `java -version`, `sdkmanager --list_installed`.
5. Créer le projet Gradle vierge et vérifier qu'il compile (`./gradlew assembleDebug` sur un projet vide).

### Phase 1 — Architecture (squelette, sans UI finale)

Entités Room, DAO, base, repositories, ViewModels vides, navigation à 5 onglets, thème de base. Livrable : projet qui compile et qui navigue entre 5 écrans vides.

### Phase 2 — Design system

Couleurs, typographies, thèmes clair/sombre, composants réutilisables (carte, état vide, dialogue, barre de recherche). Livrable : écran de démonstration des composants.

### Phase 3 — Développement, par espace

1. Home : bienvenue, date, résumé notes, tâches restantes, progression du jour, prochains événements, notes récentes, actions rapides — tout depuis Room.
2. Notes : CRUD complet, recherche, favoris, archives, catégories, couleurs, épingler, tri, éditeur riche simple.
3. Tâches : CRUD, priorités, dates limites, rappels, listes, filtres, progression.
4. Calendrier : vues mois / semaine / jour, événements CRUD, rappels, tâches à venir.
5. Profil : profil local, thème, langue, notifications, stockage, export des données (JSON), à propos, version.

Chaque espace est compilé et testé avant de passer au suivant.

### Phase 4 — Persistance et rappels

Vérification Room après redémarrage, DataStore pour les préférences, WorkManager pour les rappels de tâches et d'événements, gestion des permissions de notification (Android 13+).

### Phase 5 — Tests et qualité

Tests CRUD Room, tests de persistance, test de navigation, vérification des 5 onglets, vérification sur téléphone modeste, correction des erreurs immédiatement. Aucun bouton sans action, aucune donnée fictive en version finale.

### Phase 6 — Livraison

APK debug installable, vérifiée. Configuration de signature release préparée (keystore à créer par toi, jamais partagé ni versionné). Documentation : fonctionnalités, commandes de compilation (`./gradlew assembleDebug`, `./gradlew assembleRelease`), et procédure d'installation de l'APK sur ton téléphone.

## 5. Limitations à connaître avant de valider

- Pas de compilation possible avant l'installation de la Phase 0.
- Première compilation Gradle lente et gourmande en RAM.
- Pas d'émulateur ici : les tests visuels se feront sur ton téléphone Android avec l'APK.
- Les notifications et rappels ne peuvent être vraiment vérifiés que sur un téléphone.
- Les modèles Gemini 2.x ne sont pas concernés ici : NOTELY est native, sans IA au départ.

## 6. Ce que j'attends de toi

Dis « oui, je valide le plan » pour que je lance la Phase 0 (installation), ou dis ce que tu veux changer : couleurs, nom, ordre des écrans, langue de départ, ou modèle de départ.
