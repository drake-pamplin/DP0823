/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.2.1/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit test framework.
    testImplementation("junit:junit:4.13.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.1-jre")

    // Use Lombok annotations.
    compileOnly ("org.projectlombok:lombok:1.18.28")
	annotationProcessor ("org.projectlombok:lombok:1.18.28")
	
	testCompileOnly ("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor ("org.projectlombok:lombok:1.18.28")

    // Jupiter assertions
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    // Mockito
    testImplementation ("org.mockito:mockito-core:2.1.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("src.controller.PointOfSaleController")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
