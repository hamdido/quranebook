# quranebook

This project is an attempt to create an ebook of the Noble Quran. Arabic text goes together with translation. 




# Technical Details
For developing the ebook it is using Java and Quarkus

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
java -jar target/*-runner.jar
```

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quranebook-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## References
 - [EpubLib](https://github.com/psiegman/epublib)