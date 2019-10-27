# NearBy-Android

A sample Android application for showing near by places to user using Foursquare API

# Technologies used: 
- Kotlin
- ViewModel
- LiveData
- Room
- Retrofit
- Glide
# App main Architecture:
- Model View ViewModel (MVVM)
# App Flow
- Please check [this link](https://i.postimg.cc/Cxf9tzkP/Send-request-flow-chart.png) illustrating the flow of communication between `NearByPlacesActivity` and `NearByPlacesViewModel`
# Main Development branch:
- Develop
# Sending Photos request issue: 
- Primarily I was sending the Photos request inside the view holder itself which caused (A quota Exceeded) issue fast and the scrolling sometimes wasn't the best UX friendly
- So I did the following logic to avoid sending the data many times:
1) When the venues are retrieved, The `NearByPlacesViewModel` iterate on each `Venue` and read from database table `PhotoUrl` to see whether or not this venue has a photo url saved
2) If it didn't find a photo url in the database then it sends a request and when it responds, the `NearByPlacesViewModel` parses the data and extract the Url then add it to the database using the `venueId` as the key
3) For the view holder to fetch the url it observes on the `PhotoUrl` table using Room
