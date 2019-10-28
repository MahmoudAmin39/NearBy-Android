package com.mahmoud.nearbyandroid.data

class Constants {

    companion object {
        const val BASE_URL = "https://api.foursquare.com/v2/"
        const val CLIENT_ID = "EFZ3ARIDEFY15AFQ4MXIPXZYDH5UWYQPU3UZTAPHQ20QI5VP"
        const val CLIENT_SECRET = "IPEZJPF24PTVCD5QYEVTF3RWLCQPREZIPS4IHBXDQDJ4ZQRA"
        const val DATE_VERSION = "20200101"
        const val SHARED_PREFS = "NearBy Data"
        const val APP_MODE = "App_Mode"

        const val APPMODE_REALTIME = 1
        const val APPMODE_SINGLE_UPDATE = 2

        const val ERROR_NO_LOCATION = 3
        const val ERROR_NO_INTERNET = 4
        const val ERROR_NO_RESPONSE = 5
        const val ERROR_PERMISSION_DENIED = 6
        const val ERROR_GOOGLE_PLAY_CONNECTION_FAILED = 7
    }
}
