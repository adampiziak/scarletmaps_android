# ScarletMaps

![mit badge](https://img.shields.io/badge/license-MIT-blue)

<em>not in active development</em>

ScarletMaps is android application for helping students find their way around Rutgers campuses. Find information relevant to your location such as bus routes, bus stops and buildings. Locate bus positions and see current paths for bus routes.

ScarletMaps is free and open source. Feel free to contribute or fork :)

![demo gif](./media/demo.gif)

## Architecture
ScarletMaps utilizes a model-view-viewmodel (MVVM) architecture and is programmed in Kotlin. Dependency injection is managed with Hilt. Data is fetched using Retrofit and stored in a SQLite database using Android Room.  Complex views and lists are generated with Epoxy.

Libraries:
* Jetpack
 * Room
 * Navigation
 * AppCompat
 * Hilt
* Retrofit
* Epoxy
* Google Maps

## License

This project is licensed under the terms of the MIT license. See [LICENSE](https://github.com/adam-piziak/scarletmaps_android/blob/master/LICENSE) for more details.
