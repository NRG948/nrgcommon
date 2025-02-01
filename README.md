# FRC Team 948 - NRG Common Java Library

This repository contains common code that is shared among robots created by [FRC Team 948 - Newport Robotics Group (NRG)](https://www.nrg948.com/).

## Releases

The table below lists the published versions of the library and the WPILib release version required to use it.

| Version           | Git Tag/Branch | Required WPILib Version |
|-------------------|----------------|-------------------------|
| 2025.2.1-SNAPSHOT | main           | 2025.2.1                |
| 2025.1.0          | v2025.1.0      | 2025.1.1                |
| 2024.3.1          | v2024.3.1      | 2024.3.1                |
| 2023.4.0          | v2023.4.0      | 2023.4.2                |
| 2023.2.0          | v2023.2.0      | 2023.2.1                |
| 2022.1.0          | v2022.1.0      | 2022.4.1                |

## How to use the library in a Robot project

The library is published to GitHub Packages. Because GitHub Packages requires authentication even for installing packages, you will need to first [create a personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token). The token only needs the `packages:read` scope.

Once you have created the token, add the following entries to `$HOME/.gradle/gradle.properties` replacing `USERNAME` with your GitHub user name and `TOKEN` with the token value itself.

```properties
gpr.user=USERNAME
gpr.key=TOKEN
```

Next, add the NRG Common package repository to your `build.gradle` file. Use of the Elvis operator (`?:`) to get the GitHub Actor and Token is optional but enables the library to be consumed when the code is built during a GitHub Action.

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/NRG948/nrgcommon")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Finally, add the NRG Common Java Library as a dependency in your `build.gradle` file.

```gradle
dependencies {
    implementation 'com.nrg948:nrgcommon:2025.1.0'
}
```

> **NOTE:** If you want the latest build from `main`, use `'com.nrg948:nrgcommon:2025.2.1-SNAPSHOT'`. There may be breaking changes and it certainly will not be as stable, so use with caution.

On the next build, the library will be downloaded from GitHub packages and installed in the Gradle build cache.

## Optimizing library load time

The library uses the [`Reflections`](https://github.com/ronmamo/reflections) library to scan the Java classes for annotations. This can be a time consuming process on the original RoboRio due to its somewhat slow flash storage. To reduce load time, you can add a custom build step in your `build.gradle` to generate the annotation metadata at build time.

To generate the annotation metadata at build time, add the following build dependencies before the `plugins` section in `build.gradle`.

```gradle
buildscript {
    dependencies {
        // Add the Reflections library and dependencies to the classpath so we
        // can generate its metadata during the build.
        classpath 'org.reflections:reflections:0.10.2'
        classpath 'org.dom4j:dom4j:2.1.3'
    }
}
```

Then, add the following custom task toward the end of the `build.gradle` file.

```gradle
// Generates metadata consumed by the NRG Common Library to find annotations at runtime.
task generateReflectionsMetadata {
    dependsOn compileJava

    doLast {
        // Create a class loader for the main project files.
        Set<File> projectDirs = project.sourceSets.main.output.classesDirs.files
        URL[] projectUrls = projectDirs.collect { it.toURI().toURL() }.toArray(new URL[0])
        ClassLoader projectLoader = new URLClassLoader(projectUrls, (java.lang.ClassLoader)null)

        // Create a class loader for the project runtime dependencies.
        Set<File> classpathFiles = project.configurations.runtimeClasspath.files
        URL[] classpathUrls = classpathFiles.collect { it.toURI().toURL() }.toArray(new URL[0])
        ClassLoader classpathLoader = new URLClassLoader(classpathUrls, (java.lang.ClassLoader)null)

        // Generate the metadata for the Reflections library.
        new org.reflections.Reflections(
            new org.reflections.util.ConfigurationBuilder()
                .forPackage("frc.robot", projectLoader)
                .forPackage("com.nrg948", classpathLoader)
                .setScanners(
                    org.reflections.scanners.Scanners.FieldsAnnotated,
                    org.reflections.scanners.Scanners.MethodsAnnotated,
                    org.reflections.scanners.Scanners.SubTypes,
                    org.reflections.scanners.Scanners.TypesAnnotated))
            .save("${project.sourceSets.main.output.classesDirs.asPath}/META-INF/reflections/${project.archivesBaseName}-reflections.xml")
    }
}

// Generate the NRG Common Library metadata when the robot code is built.
jar.dependsOn generateReflectionsMetadata
```
