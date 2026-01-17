/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the__LICENSE] [1].
 */

/**
 * This is a blueprint Gradle build.gradle.kts file used by Rubens Gomes during the creation of a
 * new Gradle Spring Boot Java development project.
 *
 * @author [Rubens Gomes](https://rubensgomes.com)
 */
plugins {
    id("idea")
    id("maven-publish")
    id("version-catalog")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.release)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.task.tree)
}

// ------------------- Debug Mode -------------------
val isDebugBuild = project.hasProperty("debug") && project.property("debug") == "true"

if (isDebugBuild) {
    val versionCatalog = versionCatalogs.named("libs")
    println("Library aliases: ${versionCatalog.libraryAliases}")
    println("Bundle aliases: ${versionCatalog.bundleAliases}")
    println("Plugin aliases: ${versionCatalog.pluginAliases}")
}

// --------------- >>> dependencies <<< ---------------------------------------

dependencies {
    // ########## compileOnly ##################################################
    compileOnly("org.projectlombok:lombok")

    // ########## developmentOnly ################################################
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ########## implementation #################################################
    // Import the Spring Boot 4 BOM
    implementation(platform(libs.spring.boot.bom))
    testImplementation(platform(libs.spring.boot.bom))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // required by Spring Boot:
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // other third-party libs
    implementation("org.apache.commons:commons-lang3")
    // io.github.oshai:kotlin-logging-jvm
    implementation(libs.kotlin.logging.jvm)
    // com.rubensgomes:ms-ex-lib
    implementation(libs.ms.ex.lib)
    // com.rubensgomes:ms-reqresp-lib
    implementation(libs.ms.reqresp.lib)

    // ########## testImplementation #############################################
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.springmockk)
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle IDEA Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Idea plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/idea_plugin.html

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Java Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Java plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/java_plugin.html

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

// Disable bootJar since this is a library, not a Spring Boot application
tasks.bootJar {
    enabled = false
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to project.properties["title"],
                "Implementation-Title" to project.properties["artifactId"],
                "Implementation-Version" to project.properties["version"],
                "Implementation-Vendor" to project.properties["developerName"],
                "Built-By" to project.properties["developerId"],
                "Build-Jdk" to System.getProperty("java.home"),
                "Created-By" to
                    "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
            ),
        )
    }
}

tasks.javadoc {
    options {
        (this as StandardJavadocDocletOptions).addStringOption(
            "Xdoclint:none",
            "-quiet",
        )
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Maven Publish Plugin <<< ------------------------
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/publishing_maven.html

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") { fromResolutionOf("runtimeClasspath") }
                usage("java-runtime") { fromResolutionResult() }
            }

            groupId = project.group.toString()
            artifactId = project.findProperty("artifactId") as String
            version = project.version.toString()

            from(components["java"])

            // POM configuration
            pom {
                name = project.properties["title"] as String
                inceptionYear = "2025"
                packaging = "jar"

                licenses {
                    license {
                        name = project.properties["license"] as String
                        url = project.properties["licenseUrl"] as String
                    }
                }

                developers {
                    developer {
                        id = project.properties["developerId"] as String
                        name = project.properties["developerName"] as String
                        email = project.properties["developerEmail"] as String
                    }
                }

                scm {
                    connection = project.properties["scmConnection"] as String
                    developerConnection = project.properties["scmConnection"] as String
                    url = project.properties["scmUrl"] as String
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri(project.properties["jvmLibsRepoPackages"] as String)
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

val licenseHeaderText =
    """
    /*
     * Copyright 2026 Rubens Gomes
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * You may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    """.trimIndent()

// ----------------------------------------------------------------------------
// --------------- >>> com.diffplug.spotless Plugin <<< -----------------------
// NOTE: This section is dedicated to configuring the spotless plugin.
// ----------------------------------------------------------------------------
// https://github.com/diffplug/spotless

spotless {
    // Java formatting
    java {
        target("src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        licenseHeader(licenseHeaderText)
        importOrder("java", "javax", "org", "com", "")
        trimTrailingWhitespace()
        endWithNewline()
    }

    // Kotlin formatting
    kotlin {
        target("src/**/*.kt")
        ktfmt()
        licenseHeader(licenseHeaderText)
        trimTrailingWhitespace()
        endWithNewline()
    }

    // JSON formatting
    json {
        target("src/**/*.json")
        jackson()
    }

    // Kotlin Gradle DSL formatting (root + submodules)
    kotlinGradle {
        target("*.gradle.kts")
        // .editorconfig for fine-grained control
        ktlint().setEditorConfigPath("$rootDir/.editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> org.jetbrains.kotlin.jvm Plugin <<< --------------------
// ----------------------------------------------------------------------------
// https://kotlinlang.org/docs/gradle-configure-project.html#kotlin-and-java-sources

kotlin {
    /**
     * Java types used by Kotlin relaxes the null-safety checks. And the Spring Framework provides
     * null-safety annotations that could be potentially used by Kotlin types. Therefore, we need to
     * make jsr305 "strict" to ensure null-safety checks is NOT relaxed in Kotlin when Java
     * annotations, which are Kotlin platform types, are used.
     */
    compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") }
}

tasks.compileKotlin { dependsOn("spotlessApply") }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle JVM Test Suite Plugin <<< -----------------------
// NOTE: This section is dedicated to configuring the JVM Test Suite plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    // WARNING: If a serviceability tool is in use, please run with
    // -XX:+EnableDynamicAgentLoading to hide this warning
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

// ----------------------------------------------------------------------------
// --------------- >>> net.researchgate.release Plugin <<< --------------------
// NOTE: This section is dedicated to configuring the release plugin.
// ----------------------------------------------------------------------------
// https://github.com/researchgate/gradle-release

release {
    with(git) {
        pushReleaseVersionBranch.set("release")
        requireBranch.set("main")
    }
}

tasks.afterReleaseBuild {
    dependsOn("publish")
}

// ------------------- Debug Info -----------------------
tasks.register("debugInfo") {
    group = "help"
    description = "Prints debug information for troubleshooting build and publishing issues"

    doLast {
        println("========== DEBUG INFO ==========")

        // Project info
        println("Project: ${project.name}")
        println("Group: ${project.group}")
        println("Version: ${project.version}")
        println("Project dir: ${project.projectDir}")
        println("Build dir:  ${layout.buildDirectory.asFile.get()}")
        println("Gradle version: ${gradle.gradleVersion}")
        println("Kotlin DSL: true")

        // Java info
        println("Java version: ${System.getProperty("java.version")}")
        println("Java vendor: ${System.getProperty("java.vendor")}")
        println("Java home: ${System.getProperty("java.home")}")

        // OS info
        println("OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")} (${System.getProperty("os.arch")})")
        println("User home: ${System.getProperty("user.home")}")

        // Repositories
        println("Repositories:")
        project.repositories.forEach { repo ->
            when (repo) {
                is MavenArtifactRepository -> println(" - ${repo.name}: ${repo.url}")
                is IvyArtifactRepository -> println(" - ${repo.name}: ${repo.url}")
                is FlatDirectoryArtifactRepository -> println(" - ${repo.name}: (flat dir)")
                else -> println(" - ${repo.name}: (unknown type)")
            }
        }

        // Environment variables (print only safe ones)
        val safeEnv = listOf("GITHUB_USER", "GITHUB_TOKEN")
        println("Environment variables (safe subset):")
        safeEnv.forEach { key ->
            println(" - $key = ${System.getenv(key)}")
        }

        // Project properties
        println("Project properties:")
        project.properties.forEach { (k, v) ->
            println(" - $k = $v")
        }

        println("================================")
    }
}
