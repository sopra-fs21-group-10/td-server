# SoPra RESTful Service Template FS21


## Introduction

##  Technologies used 

##  High-level components: 

## Launch & Deployment:
### WeatherAPI
For running the application on your machine, set the environment variable WeatherKey to your OpenWeather key.

### Building with Gradle

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs21` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.


You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## Roadmap
The multiplayer part of our project unfortunately had to be abandoned. So the next big thing that can be done is to finish the multiplayer aspects of the game and make the weather have a bigger impact on the game. Beyond that, a leaderboard and spectator mode would be some of the next steps. Of course, adding more towers, minions and game modes, as well as balancing the existing ones, are things that could be worked on forever.

## Authors and acknowledgment
The main team consists of [@HuberNicolas](https://github.com/HuberNicolas) , [@Seouless29](https://github.com/Seouless29) and [@Thahit](https://github.com/Thahit). Some earlier contributer who left the team are [@maurohirt](https://github.com/maurohirt) and [@bzns](https://github.com/bzns). Special thanks go to [@demaerl](https://github.com/demaerl) and [@royru](https://github.com/royru) who were responsible for our group and of course also Professor Thomas Fritz. Also a shoutout to everyone who contributed to the template.

## License

