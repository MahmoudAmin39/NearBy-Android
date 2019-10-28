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
# Drawback of the previous approach:
- Unless the user scroll the whole list, this approach request data that even may not be used
# The final solution:
- Data are fetched from the `VenueViewHolder` so only request the bound `Venue`
- A singleton class was made for observing the Quota `QuotaObserver` with a boolean property `isQuotaAvailable`
- Whenever a response came with error code `429` which is `Quota exceeded`, `isQuotaAvailable` turns to `false`
- When the `VenueViewModel` fetch data to the `VenueViewHolder` it looks the database first using the `VenueId`
- If the `PhotoUrl` is not available and the Quota is still available, it sends a request
- If the `PhotoUrl` is not available and the Quota is not available, it emits an error to the `VenueViewHolder`
# Benefits of using the above approach:
- Requests are only sent when there is no cache and the quota is available which is the best experience the user can has
