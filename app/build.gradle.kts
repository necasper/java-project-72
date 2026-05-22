plugins {
    id("com.github.ben-manes.versions") version "0.53.0"
    java
    application
    checkstyle
    jacoco
    id("org.sonarqube") version "7.3.0.8198"
    id("com.gradleup.shadow") version "9.3.2"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filteringCharset = "UTF-8"
}

application {
    mainClass.set("hexlet.code.App")
}

tasks.named<JavaExec>("run") {
    val port = System.getenv("PORT") ?: "7070"
    environment("PORT", port)
    systemProperty("file.encoding", "UTF-8")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:7.2.0")
    implementation("io.javalin:javalin-rendering-jte:7.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.postgresql:postgresql:42.7.5")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyLocking {
    lockAllConfigurations()
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.shadowJar {
    archiveBaseName.set("HexletJavalin")
    archiveClassifier.set("all")
    mergeServiceFiles()
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
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
