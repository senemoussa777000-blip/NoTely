# NOTELY — Installation des outils (2026-11-26)

## Installé et vérifié

- Java Temurin 17.0.21.1 — `~/tools/jdk-17` — `java -version` OK
- Gradle 8.11.1 et 8.7 — `~/tools/gradle-8.11.1`, `~/tools/gradle-8.7` — `gradle --version` OK
- Android Command Line Tools 12.0 — `~/Android/Sdk/cmdline-tools/latest` — `sdkmanager --version` OK
- Android SDK : platform-tools 37.0.3 (`adb` OK), platforms android-35 (`android.jar` OK), build-tools 35.0.0 (`aapt2` OK)
- Licences SDK acceptées dans `~/Android/Sdk/licenses/`
- Variables dans `~/.bashrc` : JAVA_HOME, ANDROID_HOME, ANDROID_SDK_ROOT
- Projet vierge créé dans `~/workspace/notely/` (settings, build.gradle.kts, MainActivity Compose minimale)

## Bloquant : Gradle ne peut pas compiler

`gradle assembleDebug` échoue à chaque fois avec :
`Could not receive a message from the daemon` / `Unexpected type tag 109 found`.

Cause vérifiée par un test Java direct : toute connexion TCP directe
est interceptée dans cette machine et répond :
« muse: Other TCP connections is turned off for this assistant.
To allow it, ask the user to open Muse settings -> Permissions ->
Direct network protocols and switch other_tcp from Deny to Ask. »

C'est pour cela que le daemon Gradle (qui parle à son client en TCP
sur localhost) ne reçoit jamais la bonne réponse, et que `sdkmanager`
échoue aussi en Java. Les téléchargements par `curl` marchent, eux.

## Pour débloquer

Dans l'application Muse : Paramètres -> Permissions -> Direct network
protocols -> passer `other_tcp` de Deny à Ask. Ensuite, dire à Moise
de relancer `gradle assembleDebug`.

Contournement utilisé pour le SDK : téléchargement direct des paquets
par `curl` depuis dl.google.com et décompression manuelle dans
`~/Android/Sdk/` (sdkmanager Java ne pouvait pas télécharger).
