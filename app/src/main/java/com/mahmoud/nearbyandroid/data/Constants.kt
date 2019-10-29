package com.mahmoud.nearbyandroid.data

class Constants {

    companion object {
        const val BASE_URL = "https://api.foursquare.com/v2/"
        const val CLIENT_ID = "NNLSPAQWUMCQEGMCKLEE2ZMJE0EEETGZXUDYLDLQUDXOCRR1"
        const val CLIENT_SECRET = "CE15FDUBKXVSG33BUJ0E12PPUDTCVBIF1BTS1ADTM54GBOLH"
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
