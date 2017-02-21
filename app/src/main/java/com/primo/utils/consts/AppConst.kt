package com.primo.utils.consts

const val APP = "PRIMO"

const val GALLERY_RESULT : Int = 20;
const val TERMS_DEF : String = "terms_def.pdf";
const val TERMS_JAP : String = "terms_jap.pdf";
//const val QR_PREFIX : String = "primo.im/"

//DEV
const val QR_PREFIX : String = "http://staging.pxo.io/"
//RELEASE
//const val QR_PREFIX : String = "http://pxo.io/"

const val COUNTRIES_EN : String = "new_countries_v2.json"
const val COUNTRIES_JA : String = "new_countries_v2_ja.json"
const val COUNTRIES_CH : String = "new_countries_v2_ch.json"
const val COUNTRIES_CHT : String = "new_countries_v2_cht.json"

const val PERMISSION_CAMERA_REQUEST = 200
const val PERMISSION_LOCATION_REQUEST = 300

const val ACCESS_TOKEN = "access_token"
const val EXPIRES_IN = "expires_in"
const val USER_STATUS = "user_status"
const val CART_ID = "cart_id"
const val CREDITCARD_ID = "creditcard_id"
const val SHIPPING_ID = "shipping_id"

const val LOGIN_EMAIL = "email"
const val LOGIN_PASSWORD = "password"
const val SIGNUP_TIME = "signUpTime"
const val AUTOLOGIN_DELAY_TIME = 30 // minutes
const val USER_LANGUAGE = "user_language"
const val USER_COUNTRY = "user_country"

const val SHIPPING_ADDRESS_ADD = "shipping_address_add"
const val SHIPPING_ADDRESS_UPDATE = "shipping_address_update"
const val CREDIT_CARD_ADD = "credit_card_add"
const val CREDIT_CARD_UPDATE = "credit_card_update"
const val TAB_PROFILE_FROM_ERROR = "tab_profile_from_error"
const val TAB_ADDRESS_FROM_ERROR = "tab_address_from_error"
const val SHIPPING_ADDRESS_COUNT = "shipping_address_count"
const val CREDIT_CARD_COUNT = "credit_card_count"

const val COUNTRIES_EXIST : String = "countries_exist"

const val DELETE = 0
const val ADD = 1
const val UPDATE = 2

const val SENSITIVITY_GIGGLE: Float = 0.2f
const val TIME_WAIT: Long = 5000
const val GIGGLE_OK: Int = 1
const val GIGGLE_NO: Int = 0

//validation status for 0 stock item
const val ACTIVE = 1
const val SUCCEED = 2
const val REJECTED = 3
const val ADD_TO_WISHLIST = 4

//order or reject
const val PARSE_ORDER_REJECT = 1
const val PARSE_REJECT = 2
const val PARSE_ORDER = 3
const val OWN_ORDER_REJECT_CODE = 113
const val OWN_REJECT_CODE = 224

const val SECUREKEY = "MIIEogIBAAKCAQEArAMSgpxcKyBlYEvQJWduyYJVXwm9xVfg3nrt08nb+LxXvVfU"

//card type
const val CARD_UNKNOWN = 0
const val CARD_VISA = 1
const val CARD_MASTER = 2
const val CARD_JCB = 3
const val CARD_AMEX = 4
const val CARD_UNIPAY = 5
const val CARD_DINERS = 6
const val CARD_DISCOVER = 7