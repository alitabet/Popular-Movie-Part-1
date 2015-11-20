# Popular-Movie-Part-2
Udacity Android Nanodegree project 2: Popular Movies Part 2

The Popular Movies app retrieves movie information from [The Movie Database (TMDb)](https://www.themoviedb.org). The movies are presented to the user initially in poster format, where she can choose a specific movie to get additional details.

## Main Features

* The app starts with a container activity, where the user is presented with a grid of movie posters. This grid is original sorted by popularity. The user has the option to sort by user rating. This sorting option is available through the app settings.  
* By clicking on a movie poster the user is taken to the detail page corresponding to the clicked movie. The detail page contains additional information about the movie. Specifically, the detail page presents the movie name, release date, rating, and a plot synopsis.
* The detail page also contains a list of trailer links and user reviews. By clicking on a triler link, the user can watch the movie trailer on YouTube. In this view, the user also has the option to set a movie as a favorite movie, and to share the first movie trailer link.
* The list of favorite movies are available through the settings option
* The app has different layouts for portrait and landscape orientations. In the movie poster grid view, the number of posters per row changes from 2 to 3 when rotating the phone from portrait to landscape. This feature ensures that when a user rotates her phone to landscape, she will be able to see the movie posters at a nice and visually friendly size. In the details page, the presentation of the information gets moved to a different layout in order to enhance readability and aesthetics.
* The app provides support for both phone and tablet versions, where in the tablet case the app shows a master/detail layout.

## Tech Stuff

* The Popular Movies App implements a SyncAdapter to read movies from the Movie DB API. The adapter is scheduled to read movies once a day and update the corresponding local databases.
* The movie data is extracted from The Movie DB using [Retrofit](http://square.github.io/retrofit/).
* Locally, there are 3 databases: popular, rating, and favorites. In principle, they could all be implemented in one database. The 3 implementations just made it easier as a first app. Future work should change that into a single implementation.
* The favorites DB stores the favorite movies as chosen by the user.
* The main page is implemented using a GridView. The GridView is populated using a cursor adapter that reads from the local database.
* API Key: The Movie DB API requires a user API Key. To facilitate insertion of custom key, the user can add her own key by adding the following line to [USER_HOME]/.gradle/gradle.properties:

    ```
    MyMovieDBApiKey="UNIQUE_API_KEY"
   ```

## License:

    Copyright 2014 Ali K Thabet
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
      http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.  