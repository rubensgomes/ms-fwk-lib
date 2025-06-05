import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("idea")
  id("maven-publish")
  id("version-catalog")
  alias(ctlg.plugins.kotlin.jvm)
  alias(ctlg.plugins.kotlin.spring)
  alias(ctlg.plugins.release)
  alias(ctlg.plugins.spotless)
  alias(ctlg.plugins.spring.boot)
  alias(ctlg.plugins.spring.dependency.management)
}

val developerId: String by project
val developerName: String by project

val group: String by project
val artifact: String by project
val version: String by project
val title: String by project
val description: String by project

project.group = group

project.version = version

project.description = description

idea {
  module {
    // download javadocs and sources:
    // $ ./gradlew cleanIdea idea
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

java {
  withSourcesJar()
  withJavadocJar()
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
    vendor.set(JvmVendorSpec.AMAZON)
  }
}

// Kotlin static analyzer
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    ktfmt()
    ktlint()
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

publishing {
  publications {
    val developerEmail: String by project

    val scmConnection: String by project
    val scmUrl: String by project

    val license: String by project
    val licenseUrl: String by project

    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = artifact
      version = project.version.toString()

      from(components["java"])

      pom {
        name = title
        description = project.description
        inceptionYear = "2024"
        packaging = "jar"

        licenses {
          license {
            name = license
            url = licenseUrl
          }
        }
        developers {
          developer {
            id = developerId
            name = developerName
            email = developerEmail
          }
        }
        scm {
          connection = scmConnection
          developerConnection = scmConnection
          url = scmUrl
        }
      }
    }
  }

  repositories {
    val repsyUrl: String by project
    val repsyUsername: String by project
    val repsyPassword: String by project

    maven {
      url = uri(repsyUrl)
      credentials {
        username = repsyUsername
        password = repsyPassword
      }
    }
  }
}

// "net.researchgate.release" configuration
release {
  with(git) {
    pushReleaseVersionBranch.set("release")
    requireBranch.set("main")
  }
}

// net.researchgate.release plugin task
tasks.afterReleaseBuild { dependsOn("publish") }

tasks.jar {
  manifest {
    attributes(
        mapOf(
            "Specification-Title" to title,
            "Implementation-Title" to artifact,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to developerName,
            "Built-By" to developerId,
            "Build-Jdk" to System.getProperty("java.home"),
            "Created-By" to
                "${System.getProperty("java.version")} (${
                            System.getProperty(
                                "java.vendor"
                            )
                        })"))
  }
}

tasks.named<Test>("test") {
  // Use JUnit Platform for unit tests.
  useJUnitPlatform()
  // WARNING: If a serviceability tool is in use, please run with
  // -XX:+EnableDynamicAgentLoading to hide this warning
  jvmArgs("-XX:+EnableDynamicAgentLoading")
}

tasks.getByName<BootJar>("bootJar") { enabled = false }

tasks.getByName<Jar>("jar") { enabled = true }

dependencies {
  // ########## annotationProcessor ############################################
  annotationProcessor("org.projectlombok:lombok")

  // ########## compileOnly ##################################################
  compileOnly("org.projectlombok:lombok")

  // ########## implementation #################################################
  implementation("org.springframework.boot:spring-boot-starter-hateoas")
  implementation("org.springframework.boot:spring-boot-starter-web")
  // required by Spring Boot:
  implementation(ctlg.kotlin.reflect)
  implementation(ctlg.kotlin.stdlib)
  implementation(ctlg.jackson.module.kotlin)
  // other third-party libs
  implementation("org.apache.commons:commons-lang3")
  implementation(ctlg.jakarta.validation.api)
  implementation(ctlg.reqresp.lib)
  implementation(ctlg.bundles.logbackext)

  // ########## developmentOnly ################################################
  developmentOnly("org.springframework.boot:spring-boot-devtools")

  // ########## testImplementation #############################################
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation(ctlg.bundles.jakarta.bean.validator)
  testImplementation(ctlg.springmockk)
}
