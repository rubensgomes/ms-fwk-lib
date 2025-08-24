# ms-fwk-lib

A Spring Boot infra-structure framework library for microservices.

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
./gradlew --info clean test
```

```shell
git commit -m "updated gradle-catalog" -a
git push
```

```shell
# only Rubens can release
./gradlew --info release
```

```shell
git checkout release
git pull
./gradlew --info publish
git checkout main
```

## IntelliJ - GitHub Copilot `mcp.json`

```bash
# location of mcp.json
cat ~/.config/github-copilot/intellij/mcp.json
```

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
