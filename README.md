### Hexlet tests and linter status:
[![Actions Status](https://github.com/necasper/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/necasper/java-project-72/actions)

### Сборка CI

[![build](https://github.com/necasper/java-project-72/actions/workflows/build.yml/badge.svg)](https://github.com/necasper/java-project-72/actions/workflows/build.yml)

### SonarCloud

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=necasper_java-project-72&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=necasper_java-project-72)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=necasper_java-project-72&metric=coverage)](https://sonarcloud.io/summary/new_code?id=necasper_java-project-72)

### Задеплоенное приложение

[Демо на Render](https://java-project-72.onrender.com)

### Запуск локально

Порт задаётся переменной окружения **`PORT`** (для Render её выставляет платформа). Если не задана, используется `7070`.

Из каталога `app`: `./gradlew run` (Gradle передаёт `PORT` в процесс приложения; при необходимости задайте её в оболочке, например `PORT=8080 ./gradlew run` в Unix или `$env:PORT=8080; .\gradlew.bat run` в PowerShell).
