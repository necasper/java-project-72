plugins {
    id("com.github.ben-manes.versions") version "0.53.0"
    java
    checkstyle
    jacoco
    id("org.sonarqube") version "7.3.0.8198"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyLocking {
    lockAllConfigurations()
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

sonar {
    properties {
        val projectKey = System.getenv("SONAR_PROJECT_KEY")?.takeIf { it.isNotBlank() }
            ?: "necasper_java-project-72"
        val organization = System.getenv("SONAR_ORGANIZATION")?.takeIf { it.isNotBlank() }
            ?: "necasper"
        property("sonar.projectKey", projectKey)
        property("sonar.organization", organization)
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        System.getenv("SONAR_TOKEN")?.takeIf { it.isNotBlank() }?.let { token ->
            property("sonar.token", token)
            property("sonar.login", token)
        }
    }
}
