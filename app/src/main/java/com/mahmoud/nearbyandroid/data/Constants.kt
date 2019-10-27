package com.mahmoud.nearbyandroid.data

class Constants {

    companion object {
        const val BASE_URL = "https://api.foursquare.com/v2/"
        const val CLIENT_ID = "245D3ZOCUVGK2RB421JTM2RLNU21KYOOYGJ2JTHA2CBWNQMQ"
        const val CLIENT_SECRET = "Z2FR1Q0D534LQLL5HAHQUWMGQXN5WSDDYQV4TBESUGH2LVMJ"
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
