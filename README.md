# FRC Team 948 - NRG Common Java Library

This repository contains common code that is shared among robots created by [FRC Team 948 - Newport Robotics Group (NRG)](https://www.nrg948.com/).

## Releases

The table below lists the published versions of the library and the WPILib release version required to use it.

| Version           | Git Tag/Branch | Required WPILib Version |
|-------------------|----------------|-------------------------|
| 2022.1.1-SNAPSHOT | main           | 2022.4.1                |
| 2022.1.0          | v2022.1.0      | 2022.4.1                |

## How to use the library in a Robot project

The library is published to GitHub Packages. Because GitHub Packages requires authentication even for installing packages, you will need to first [create a personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token). The token only needs the `packages:read` scope.

Once you have created the token, add the following entries to `$HOME/.gradle/gradle.properties` replacing `USERNAME` with your GitHub user name and `TOKEN` with the token value itself. Use of the Elvis operator (`?:`) to get the GitHub Actor and Token is optional but enables the library to be consumed when the code is built during a GitHub Action.

```properties
gpr.user=USERNAME
gpr.key=TOKEN
```

Next, add the NRG Common package repository to your `build.gradle` file.

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/edreed/nrgcommon")
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
    implementation 'com.nrg948:nrgcommon:2022.1.0'
}
```

On the next build, the library will be downloaded from GitHub packages and installed in the Gradle build cache. You can also explicitly install the library using the following command from the root of your robot repository.

```powershell
$ ./gradlew install
```
