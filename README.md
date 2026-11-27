# NOTELY

« Tes idées, toujours à portée de main. »

Application Android native de productivité : notes, tâches et calendrier
dans un seul espace, hors ligne d'abord.

## Fonctionnalités

- **Accueil** : date du jour, nombre de notes, tâches restantes et
  progression, prochains événements, notes récentes, actions rapides.
- **Notes** : créer, modifier, supprimer, recherche instantanée,
  favoris, archives, couleurs, épingler, tri par date ou par titre.
- **Tâches** : priorités (basse / moyenne / haute), date limite,
  rappel 1 h avant, filtres (toutes, aujourd'hui, à venir, terminées),
  barre de progression.
- **Calendrier** : vue mensuelle, événements du jour sélectionné,
  créer / modifier / supprimer un événement (date, heures de début et
  de fin, rappel 15 min avant).
- **Profil** : thème clair / sombre / système, notifications,
  langue (français), stockage, export des données en JSON, à propos,
  version 1.0.0.

## Technique

Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room,
DataStore, WorkManager, MVVM. Base vide au premier lancement, aucune
donnée fictive, aucun bouton sans action.

Plan complet : [PLAN-NOTELY.md](PLAN-NOTELY.md)
Installation des outils : [INSTALLATION.md](INSTALLATION.md)

## Compilation

```bash
export JAVA_HOME=/home/hatch/tools/jdk-17
export ANDROID_HOME=/home/hatch/Android/Sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH
cd ~/workspace/notely
gradle assembleDebug
```

L'APK se trouve ensuite dans `app/build/outputs/apk/debug/app-debug.apk`.
À installer sur un téléphone Android pour tester.

## État actuel (2026-11-26)

- Code complet des phases 1 à 3 du plan : données, design system,
  navigation, 5 écrans et leurs ViewModels.
- Compilation non vérifiée ici : le daemon Gradle ne peut pas parler
  à son client dans cette machine (connexions TCP directes refusées,
  `other_tcp = Deny`). Voir INSTALLATION.md : passer `other_tcp` à
  Ask dans Muse, Paramètres → Permissions → Direct network protocols,
  puis relancer `gradle assembleDebug`.

## Reste à faire

- Intégrer les polices Plus Jakarta Sans (titres) et Inter (textes)
  dans `res/font/` et les déclarer dans `ui/theme/Type.kt`.
- Tester les rappels (WorkManager) et les notifications sur un vrai
  téléphone.
- Tester la navigation, la persistance après redémarrage et l'export
  JSON sur téléphone.
- Préparer la signature release (keystore, jamais partagé).
