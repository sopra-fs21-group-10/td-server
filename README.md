# SoPra RESTful Service Template FS21


## Introduction
The aim of our project was to build a 2D-Towerdefense game. In case you don't know the genre yet, [here is a nice video](https://datasaurus-rex.com/inspiration/storytelling/tower-defense-games-explained), that explains the most important aspects. Furthermore, the application has to consist of a front-end and back-end, which are usign the REST-protocol. In addtion to this, an external API has to be used, too.

##  Technologies used 
The language used for the backend is java. The database is managed with jpa. Other important libararies/API's were spring boot, the openweathermap APi, Mockito for the tests etc.

##  High-level components
The main compontens are the controllers, which are responsible for interacting with the frontend, the services, which are responsible for executing what the client asked for, and the database.

## Launch & Deployment
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
Here is a list with ideas to improve/extend the project further:
- Adding a Multiplayer (and/or design an AI such that you can play vs. the computer)
- Implement the originally idea, where 2 (or even more?) players are in a game. Besides that you can buy and place towers, each player also can spend money for buying minions. These minions will be sent to the opponents board. This would add a new layer of tactical decisions.
- making the weather have a bigger impact on the game, as planned
- Adding more maps (and/or write a mapgenerator)
- Adding more towers/minions (this would also need a lot of balancing)



### Authors and acknowledment
This project was created during the "Softwarepraktikum" Sopra at the University of Zürich (UZH) in the Spring Semester 21. The core of the team was:
- [Nicolas Blumer](https://github.com/Thahit), [Nicolas Huber](https://github.com/HuberNicolas) and [Michael Vuong](https://github.com/Seouless29)

We would also like to mention that the project started initially among Louis Huber and Omar Abo Hamida. Due to their high workload, they decided to leave the group after the inital assessment. Nevertheless, thank you Louis and Omar for your initial support!

After this assessment, Matej Gurica and Mauro Dörig joined our team. Unfortunately, Matej and Mauro also decided to leave the project after a couple of weeks (between M3 and M4). Anyway, thank you 2 for your effort.

Special thanks go to [Marion Dübendorfer](https://github.com/demaerl) and [Roy Rutishauser](https://github.com/royru) who were responsible for our group and of course also Professor Thomas Fritz. Also a shoutout to everyone who contributed to the template.



## License
GNU General Public License v3.0
