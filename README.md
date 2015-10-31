# Popular-Movie-Part-2
Udacity Android Nanodegree project 2: Popular Movies Part 2

The Popular Movies app retrieves movie information from [The Movie Database (TMDb)](https://www.themoviedb.org). The movies are presented to the user initially in poster format, where she can choose a specific movie to get additional details.

## Main Features

* The app starts with a container activity, where the user is presented with a grid of movie posters. This grid is original sorted by popularity. The user has the option to sort by user rating. This sorting option is available through the app settings.  
* By clicking on a movie poster the user is taken to the detail page corresponding to the clicked movie. The detail page contains additional information about the movie. Specifically, the detail page presents the movie name, release date, rating, and a plot synopsis.
* The app has different layouts for portrait and landscape orientations. In the movie poster grid view, the number of posters per row changes from 2 to 3 when rotating the phone from portrait to landscape. This feature ensures that when a user rotates her phone to landscape, she will be able to see the movie posters at a nice and visually friendly size. In the details page, the presentation of the information gets moved to a different layout in order to enhance readability and aesthetics.

## Tech Stuff

* The main page is implemented using a GridView. The GridView implements an endless scroll list in order to keep retrieving data as the user reaches the end of the list. The adapter attached to the GridView is a custom ImageAdapter that extends Android's ArrayAdapter. This custom adapter provides an easy way to set the movie posters in the GridView. 
* The movie data is extracted from The Movie DB using an AsyncTask. Although a lot of the features to establish and manage an HTTP connection can be better done using third party software like [Retrofit](http://square.github.io/retrofit/), it was a better learning experience to do it the classical way here. During the second part of the project, more third party software will be included if necessary.
* In order to efficiently pass information from the main to the detail activity, the movie data is stored in a custom class called MovieItem. The MovieItem implements a Parcelable object, which allows sending complete movie information from the main to the detail activity during the intent call. This was implemented mainly to avoid having to create another AsyncTask in the detail activity to retrieve the movie data. The detail activity will only use the movie thumbnail URL and fetch it using Picasso. During part 2 of the project, we will be using SQLite and this feature might not be necessary.
*  API Key: The Movie DB API requires a user API Key. To facilitate insertion of custom key, the file keys.xml provides an interface for key insertion. This file is located under the values folder in the app resources. Below is a look at the keys.xml file:
 
    ```
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <!-- API Key. Placed here to be ignored during github push -->
        <string name="api_key">INSERT API KEY HERE</string>
    </resources>
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