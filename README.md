# ms-framework-lib

A Spring infra-structure framework library for microservices.
This library implements the following features:

- parses incoming requests and stores common attributes in the MDC context to be
  used in logging headers.

## Display Java Tools Installed

```shell
./gradlew -q javaToolchains
```

## Clean, Build, Test, Assemble, Publish, Release

```shell
./gradlew --info clean
```

```shell
./gradlew :lib:spotlessApply
```

```shell
./gradlew --info build
```

```shell
./gradlew --info check
```

```shell
./gradlew --info jar
```

```shell
./gradlew --info assemble
```

```shell
# only Rubens can publish
./gradlew --info publish
```

```shell
# only Rubens can release
./gradlew --info release
```

## usage

### ~/.gradle/gradle.properties

- make these system properties to be read from settings.gradle.kts
  systemProp.repsyUsername=rubensgomes
  systemProp.repsyPassword=<RESTRICTED>

### settings.gradle.kts

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()

        maven {
            url = uri("https://repo.repsy.io/mvn/rubensgomes/default/")
            credentials {
                username = System.getProperty("repsyUsername")
                password = System.getProperty("repsyPassword")
            }
        }
    }

    versionCatalogs {
        create("ctlg") {
            from("com.rubensgomes:gradle-catalog:0.0.37")
        }
    }
}
```

### build.gradle.kts

```kotlin

plugins {
    // ...
    id("version-catalog")
    // ...
}

// ...
dependencies {
    implementation(ctlg.msfmk.lib)
}
// ...
```

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
