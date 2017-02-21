/**
 * Changes:
 *
 * - Parse status value
 * - Add Reject parser
 * - Change CartItem and WishItem for merchant info
 *
 * 2015 © Primo . All rights reserved.
 */

package com.primo.network.parsers

import android.util.Log
import com.primo.main.MainClass
import com.primo.network.new_models.*
import com.primo.utils.*
import com.primo.utils.consts.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


object PrimoParsers {

    fun dataParser(response: String): String {

        var data = ""

        try {

            val jsonObject = JSONObject(response)
            data = jsonObject.getString("data", "")

        } catch(ex: JSONException) {
            ex.printStackTrace()
        }

        return data
    }

    fun errorParser(response: String): NetworkException {

        val message = ""
        var code = -1

        try {

            val jsonObject = JSONObject(response)
            code = jsonObject.getInt("error_code", -1)

        } catch(ex: JSONException) {
            ex.printStackTrace()
        }

        return NetworkException(message, code)
    }

    fun authParser(data: String): Auth? {

        var auth: Auth? = null

        try {

            val jsonObject = JSONObject(data)

            val access_token = jsonObject.getString("access_token", "")
            val expires_in = jsonObject.getLong("expires_in", -1)
            val user_status = jsonObject.getInt("user_status", -1)
            val cart_id = jsonObject.getString("cart_id", "")
            val creditcard_id = jsonObject.getString("creditcard_id", "")
            val shipping_id = jsonObject.getString("shipping_id", "")
            val country = jsonObject.getInt("country", -1)

            auth = Auth(access_token, expires_in, user_status, cart_id, creditcard_id, shipping_id, country)

        } catch(ex: JSONException) {
            ex.printStackTrace()
        }

        return auth
    }

    fun userProfileParser(data: String): UserProfile? {

        var userProfile: UserProfile? = null

        try {

            val jsonObject = JSONObject(data)
            Log.d("Test", "user profile retrieved:" + data)

            val email = jsonObject.getString("email", "")
            val phone = jsonObject.getString("phone", "")
            val cell_phone = jsonObject.getString("cell_phone", "")
            val firstname = jsonObject.getString("firstname", "")
            val lastname = jsonObject.getString("lastname", "")
            val address = jsonObject.getString("address", "")
            val city = jsonObject.getString("city", "")
            val state = jsonObject.getString("state", "")
            val country = jsonObject.getInt("country", -1)
            val postcode = jsonObject.getString("postcode", "")
            val delivery_preference = jsonObject.getInt("delivery_preference", 2)
            val is_mail_campaign = jsonObject.getInt("is_mail_campaign", 0)

            userProfile = UserProfile(email, phone, cell_phone, firstname, lastname, address, city,
                    state, country, postcode, delivery_preference, is_mail_campaign, 0, "")

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return userProfile
    }

    fun creditCardParser(data: String): CreditCard? {

        var creditCard: CreditCard? = null

        try {

            val jsonObject = JSONObject(data)

            val creditcard_id = jsonObject.getString("creditcard_id", "")
            val last_four = jsonObject.getString("last_four", "")
            val cardname = jsonObject.getString("cardname", "")
            val cardyear = jsonObject.getString("cardyear", "")
            val cardmonth = jsonObject.getString("cardmonth", "")
            val is_default = jsonObject.getInt("is_default", 0)

            creditCard = CreditCard(creditcard_id, cardname, cardyear, cardmonth, last_four, is_default)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return creditCard
    }

    fun productParser(data: String): Product? {

        var product: Product? = null

        try {

            Log.d("Test", "product parser:" + data)
            val jsonObject = JSONObject(data)

            val detailObject = jsonObject.getJSONObject("detail", JSONObject())

            val productId = jsonObject.getString("product_id", "")

            val productName = detailObject.getString("product_name", "")
            val currency = detailObject.getInt("currency", 0)
            val price = detailObject.getDouble("price", 0.0)
            val category = detailObject.getInt("category", 0)
            val variantType = detailObject.getInt("variant_type", 0)
            val weightTypeUnit = detailObject.getInt("weight_type_unit", 0)
            val weightAmount = detailObject.getDouble("weight_amount", 0.0)
            val description = detailObject.getString("description", "")
            val zeroPriceAction = detailObject.getInt("zero_price_action", 0)
            val minimumOrderQty = detailObject.getInt("minimum_order_qty", 0)
            val maximumOrderQty = detailObject.getInt("maximum_order_qty", 0)
            val taxType = detailObject.getInt("tax_type", 0)
            val taxAmount = detailObject.getDouble("tax_amount", 0.0)
            val creationDate = detailObject.getString("creation_date", "")
            val availSinceDate = detailObject.getString("avail_since_date", "")
            val outOfStockAction = detailObject.getInt("out_of_stock_action", 0)

            // IMAGE PARSING

            var defaultImage = ""
            var defaultThumbnail = ""

            val images = mutableListOf<Image>()

            val imagesArr = jsonObject.getJSONArrayExt("images")

            val imagesSize = imagesArr.length()

            for (i in 0..imagesSize - 1) {
                val image = imageParser(imagesArr.getString(i))
                if (image != null) {
                    images.add(image)
                    if (image.is_default == 1) {
                        defaultImage = image.image_url
                        defaultThumbnail = image.image_thumbnail_url
                    }
                }
            }

            val shipping = jsonObject.getJSONObject("shipping", JSONObject())

            val shippingDomOption = shipping.getInt("shipping_dom_option", 0)
            val shippingDomAmount = shipping.getDouble("shipping_dom_amount", 0.0)
            val shippingInterOption = shipping.getInt("shipping_inter_option", 0)
            val shippingInterAmount = shipping.getDouble("shipping_inter_amount", 0.0)

            // STOCKS

            val stocksArr = jsonObject.getJSONArrayExt("stocks")
            val stocksSize = stocksArr.length()
            val stocks = mutableListOf<Stock>()

            for (i in 0..stocksSize - 1) {
                val stock = stockParser(stocksArr.getString(i))
                if (stock != null)
                    stocks.add(stock)
            }

            // DISCOUNTS

            val discountsArr = jsonObject.getJSONArrayExt("discounts")
            val discountsSize = discountsArr.length()
            val discounts = mutableListOf<Discount>()

            for (i in 0..discountsSize - 1) {
                val discount = discountParser(discountsArr.getString(i))
                if (discount != null)
                    discounts.add(discount)
            }

            product = Product(productId, productName, currency, price, category, variantType,
                    weightTypeUnit, weightAmount, description, zeroPriceAction, minimumOrderQty,
                    maximumOrderQty, taxType, taxAmount, creationDate, availSinceDate,
                    outOfStockAction, defaultImage, defaultThumbnail, shippingDomOption,
                    shippingDomAmount, shippingInterOption, shippingInterAmount, images, discounts, stocks)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return product
    }

    fun cartItemParser(data: String): CartItem? {

        var cartItem: CartItem? = null

        try {

            val jsonObject = JSONObject(data)
            Log.d("Test", "cart item parser:" + data)

            val productId = jsonObject.getString("product_id", "")
            val cartItemId = jsonObject.getString("cart_item_id", "")
            val status = jsonObject.getInt("status", 1)
            val quantity = jsonObject.getInt("quantity", 1)
            val productName = jsonObject.getString("product_name", "")

            val defaultImageObj = jsonObject.getJSONObject("default_image", JSONObject())
            val imageUrl = defaultImageObj.getString("image_url", "")
            val thumbnailUrl = defaultImageObj.getString("image_thumbnail_url", "")

            val stock = stockParser(jsonObject.getString("stock", "")) ?: Stock()

            //Merchant Parsing
            val merchantObject = jsonObject.getJSONObject("merchant", JSONObject())
            val merchantName = merchantObject.getString("merchant_name", "")
            val country = merchantObject.getString("country", "")
            val url = merchantObject.getString("url", "")
            //end

            val total_price = jsonObject.getDouble("total_price", 0.0)
            val total_shipping = jsonObject.getDouble("total_shipping", 0.0)
            val total_discount = jsonObject.getDouble("total_discount", 0.0)
            val final_price = jsonObject.getDouble("final_price", 0.0)

            val dataSupportObject = jsonObject.getJSONObject("data_support", JSONObject())
            val currency = dataSupportObject.getInt("currency", 0)
            val price = dataSupportObject.getDouble("price", 0.0)
            val description = dataSupportObject.getString("description", "")
            val shippingDomOption = dataSupportObject.getInt("shipping_domestic_option", 0)
            val shippingDomAmount = dataSupportObject.getDouble("shipping_domestic_amount", 0.0)
            val shippingInterOption = dataSupportObject.getInt("shipping_international_option", 0)
            val shippingInterAmount = dataSupportObject.getDouble("shipping_international_amount", 0.0)

            cartItem = CartItem(productId, cartItemId, status, quantity, productName, imageUrl, thumbnailUrl, stock,
                    price, currency, description, shippingDomOption, shippingDomAmount,
                    shippingInterOption, shippingInterAmount, merchantName, country, url, total_price, total_shipping, total_discount, final_price)
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return cartItem
    }

    fun wishesParser(data: String): MutableList<WishItem> {

        val wishes = mutableListOf<WishItem>()

        try {

            val arr = JSONArray(data)
            val length = arr.length()

            for (i in 0..length - 1) {
                val item = wishItemParser(arr.getString(i))
                item?.let { wishes.add(item) }
            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return wishes
    }

    fun wishItemParser(data: String): WishItem? {

        var wishItem: WishItem? = null

        try {

            Log.d("Test", "wish item parser:" + data)
            val jsonObject = JSONObject(data)

            val quantity = jsonObject.getInt("quantity", 0)
            val wishlistId = jsonObject.getString("wishlist_id", "")

            val productObj = jsonObject.getJSONObject("product", JSONObject())

            val productId = productObj.getString("product_id", "")

            val detailObject = productObj.getJSONObject("detail", JSONObject())

            //Merchant Parsing
            val merchantObject = productObj.getJSONObject("merchant", JSONObject())
            val merchantName = merchantObject.getString("merchant_name", "")
            val country = merchantObject.getString("country", "")
            val url = merchantObject.getString("url", "")
            //end

            val productName = detailObject.getString("product_name", "")
            val currency = detailObject.getInt("currency", 0)
            val price = detailObject.getDouble("price", 0.0)
            val category = detailObject.getInt("category", 0)
            val variantType = detailObject.getInt("variant_type", 0)
            val weightTypeUnit = detailObject.getInt("weight_type_unit", 0)
            val weightAmount = detailObject.getDouble("weight_amount", 0.0)
            val description = detailObject.getString("description", "")
            val zeroPriceAction = detailObject.getInt("zero_price_action", 0)
            val minimumOrderQty = detailObject.getInt("minimum_order_qty", 0)
            val maximumOrderQty = detailObject.getInt("maximum_order_qty", 0)
            val taxType = detailObject.getInt("tax_type", 0)
            val taxAmount = detailObject.getDouble("tax_amount", 0.0)
            val creationDate = detailObject.getString("creation_date", "")
            val availSinceDate = detailObject.getString("avail_since_date", "")
            val outOfStockAction = detailObject.getInt("out_of_stock_action", 0)

            val defaultImageObj = productObj.getJSONObject("default_image", JSONObject())
            val imageUrl = defaultImageObj.getString("image_url", "")
            val thumbnailUrl = defaultImageObj.getString("image_thumbnail_url", "")

            val stock = stockParser(jsonObject.getString("stock", "")) ?: Stock()

            wishItem = WishItem(productId, wishlistId, quantity, productName, category, variantType,
                    weightTypeUnit, weightAmount, zeroPriceAction, minimumOrderQty, maximumOrderQty,
                    taxType, taxAmount, creationDate, availSinceDate, outOfStockAction, imageUrl,
                    thumbnailUrl, stock, price, currency, description, merchantName, country, url)
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return wishItem
    }


    fun imageParser(data: String): Image? {

        var image: Image? = null

        try {

            val jsonObj = JSONObject(data)

            val is_default = jsonObj.getInt("is_default", 0)
            val caption = jsonObj.getString("caption", "")
            val display_order = jsonObj.getInt("display_order", 0)
            val image_url = jsonObj.getString("image_url", "")
            val image_thumbnail_url = jsonObj.getString("image_thumbnail_url", "")

            image = Image(is_default, caption, display_order, image_url, image_thumbnail_url)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return image
    }

    fun discountParser(data: String): Discount? {

        var discount: Discount? = null

        try {

            val jsonObj = JSONObject(data)

            val quantity = jsonObj.getInt("quantity", 0)
            val amount = jsonObj.getDouble("amount", 0.0)
            val discount_type = jsonObj.getInt("discount_type", 0)

            discount = Discount(quantity, amount, discount_type)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return discount
    }

    fun stockParser(data: String): Stock? {

        var stock: Stock? = null

        try {

            val jsonObj = JSONObject(data)

            val variant_type = jsonObj.getInt("variant_type", 0)
            val stock_id = jsonObj.getString("stock_id", "")
            val quantity = jsonObj.getInt("quantity", 0)

            //SIZE
            val sizeOption = sizeParser(stock_id, jsonObj.optString("size", ""))

            //COLOR
            val colorOption = colorParser(stock_id, jsonObj.optString("color", ""))

            //CUSTOM
            //val custom = jsonObj.getString("custom", "")

            stock = Stock(variant_type, quantity, stock_id, sizeOption, colorOption, Option())

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return stock
    }

    fun colorParser(stockId: String, data: String): Option {

        try {
            val colorObj = JSONObject(data)
            val colorCode = colorObj.getString("color_code", "")
            val colorName = colorObj.getString("name", "")
            return Option(colorName, colorCode, stockId)
        } catch (jex: JSONException) {
        }

        return Option()
    }

    fun sizeParser(stockId: String, data: String): Option {

        try {
            val sizeObj = JSONObject(data)
            val sizeName = sizeObj.getString("name", "")
            val sizeDescription = sizeObj.getString("description", "")
            return Option(sizeName, sizeDescription, stockId)
        } catch (jex: JSONException) {
        }

        return Option()
    }

    fun cartParser(data: String): Cart? {

        var cart: Cart? = null

        try {

            val jsonObj = JSONObject(data)
            Log.d("Test", " === cart detail:" + jsonObj)

            val unique_id = jsonObj.getString("unique_id", "")
            val cart_id = jsonObj.getString("cart_id", "")

            //save updated cartID
            MainClass.getSharedPreferences().edit().putString(CART_ID, cart_id).apply()

            val id = if (unique_id.isEmpty()) cart_id else unique_id

            val cartListsArr = jsonObj.getJSONArrayExt("cart_items")
            val cartSize = cartListsArr.length()
            val productList = mutableListOf<CartItem>()

            for (i in 0..cartSize - 1) {
                val stock = cartItemParser(cartListsArr.getString(i))
                if (stock != null)
                    productList.add(stock)
            }

            cart = Cart(id, productList)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return cart
    }

    fun cartsParser(data: String): String {

        var carts: String = ""

        try {

            val jsonObj = JSONObject(data)
            carts = jsonObj.getJSONObject("carts", JSONObject()).toString()

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return carts
    }

    fun stockListParser(data: String): ArrayList<Stock> {

        val list = arrayListOf<Stock>()

        try {

            val arr = JSONArray(data)
            val stocks = arr.getJSONObject(0).getJSONArray("stocks")
            val size = stocks.length()

            for (i in 0..size - 1) {
                val stock = stockParser(stocks.getString(i))
                if (stock != null)
                    list.add(stock)
            }

        } catch (jex: JSONException) {
            jex.printStackTrace()
        }

        return list
    }

    fun countParser(data: String): Int {

        var result = 0

        try {
            val jsonObject = JSONObject(data)
            val rejects = jsonObject.getJSONArrayExt("rejects")
            val orders = jsonObject.getJSONArrayExt("orders")

            if (rejects.length() > 0 && orders.length() > 0)
                result = PARSE_ORDER_REJECT
            else if (rejects.length() > 0 && orders.length() == 0)
                result = PARSE_REJECT
            else if (orders.length() > 0 && rejects.length() == 0)
                result = PARSE_ORDER

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return result
    }

    fun orderParser(data: String): Boolean {

        var isOrderSuccess = false

        try {

            val jsonObject = JSONObject(data)
            //val rejects = jsonObject.getJSONArrayExt("rejects")
            val orders = jsonObject.getJSONArrayExt("orders")

            val size = orders.length()
            val success = mutableListOf<Int>()

            for (i in 0..size - 1) {
                val statusItem = orders.getJSONObject(i)
                val status = statusItem.getInt("payment_status", 0)
                if (status in 1..2) {
                    success.add(status)
                }
            }

            isOrderSuccess = success.size > 0

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return isOrderSuccess
    }

    fun rejectParser(data: String): ArrayList<RejectItem> {

        val reject_items = arrayListOf<RejectItem>()

        try {

            val dataObj = JSONObject(PrimoParsers.dataParser(data))
            Log.d("Test", "only reject data:" + dataObj)
            val rejectDataArr = dataObj.getJSONArrayExt("rejects")

            val size = rejectDataArr.length()
            for (i in 0..size - 1) {

                val rejectDataItem = rejectDataArr.getJSONObject(i)

                val code = rejectDataItem.getInt("code")
                val items = rejectDataItem.getJSONObject("item")
                val product_id = items.getString("product_id")
                val product_name = items.getString("product_name")

                val images = items.getJSONObject("default_image")
                val product_image_url = images.getString("image_url")
                val product_thumbnail_url = images.getString("image_thumbnail_url")

                val reject_item =  RejectItem(product_id, product_name, product_image_url, product_thumbnail_url, code)
                reject_items.add(reject_item)
            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return reject_items
    }

    fun orderRejectParser(data: String): ArrayList<RejectItem> {

        val items = arrayListOf<RejectItem>()

        try {

            val dataObj = JSONObject(PrimoParsers.dataParser(data))
            val rejectDataArr = dataObj.getJSONArrayExt("rejects")
            val orderDataArr  = dataObj.getJSONArrayExt("orders")

            val size = orderDataArr.length()
            for (i in 0..size - 1) {

                val orderDataItem = orderDataArr.getJSONObject(i)
                val rejectDataItem = rejectDataArr.getJSONObject(i)

                val code = rejectDataItem.getInt("code")

                val details = orderDataItem.optJSONArray("details")
                if (details.length() > 0) {

                    val detailObject = details.getJSONObject(0)

                    val supportObject = detailObject.optJSONObject("data_support")

                    val productName = supportObject.optString("product_name", "")

                    val productImageUrl = supportObject.optString("product_image_url", "")
                    val productThumbnailUrl = supportObject.optString("product_image_thumbnail_url", "")

                    val product = supportObject.optJSONObject("product")
                    val productId = product.optString("product_id", "")

                    val item = RejectItem(productId, productName, productImageUrl, productThumbnailUrl, code)

                    items.add(item)
                }

            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return items
    }

    fun orderHistoryParser(data: String): ArrayList<CartItem> {

        val items = arrayListOf<CartItem>()

        try {

            val dataObj = JSONObject(data)
            val dataArr = dataObj.optJSONArray("data")

            val size = dataArr.length()
            for (i in 0..size - 1) {

                val dataItem = dataArr.getJSONObject(i)

                val details = dataItem.optJSONArray("details")

                if (details.length() > 0) {

                    val detailObject = details.getJSONObject(0)

                    var description: String

                    val price = detailObject.optDouble("price", 0.0)
                    val currency = detailObject.optInt("currency", 0)
                    description = detailObject.optString("description", "")

                    val supportObject = detailObject.optJSONObject("data_support")

                    val productName = supportObject.optString("product_name", "")

                    if (description.isEmpty())
                        description = supportObject.optString("description", "")

                    val productImageUrl = supportObject.optString("product_image_url", "")

                    val product = supportObject.optJSONObject("product")
                    val productId = product.optString("product_id", "")


                    val stockObject = detailObject.optJSONObject("stock")
                    val stockId = stockObject.optString("stock_id", "")

                    val stock = Stock()
                    stock.stock_id = stockId

                    val item = CartItem(productId, ""/*empty*/, ACTIVE, 1, productName, productImageUrl, productImageUrl, stock, price, currency, description, -1, 0.0, -1, 0.0)

                    items.add(item)
                }

            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return items
    }

    fun postCodeParser(data: String): Address? {

        var postCode: Address? = null

        try {

            val jsonObject = JSONObject(data)

            val prefecture = jsonObject.getString("prefecture_kanji", "")
            val city = jsonObject.getString("city_kanji", "")
            val town = jsonObject.getString("town_kanji", "")

            postCode = Address(prefecture, city, town)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return postCode
    }

    fun listShippingAddressParser(data: String): ArrayList<UserProfile> {

        val shippingAddress_items = arrayListOf<UserProfile>()

        try {

            val dataObj = JSONObject(data)
            val dataArr = dataObj.optJSONArray("data")
            Log.d("Test", "shipping address array:" + dataArr)

            val size = dataArr.length()
            for (i in 0..size - 1) {

                val item = dataArr.getJSONObject(i)

                val firstname   = item.getString("firstname")
                val lastname    = item.getString("lastname")
                val phone       = item.getString("phone")
                val shipping_id = item.getString("shipping_id")
                val post_code   = item.getString("postcode")
                val address     = item.getString("address")
                val city        = item.getString("city")
                val state       = item.getString("state")
                val country     = item.getInt("country")
                val is_default  = item.getInt("is_default")

                val shippingAddress_item =  UserProfile("", phone, "", firstname, lastname, address, city, state, country, post_code, 2, 1, is_default, shipping_id)
                shippingAddress_items.add(shippingAddress_item)
            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return shippingAddress_items
    }

    fun listCreditCardParser(data: String): ArrayList<CreditCardData> {

        val creditCard_items = arrayListOf<CreditCardData>()

        try {

            val dataObj = JSONObject(data)
            val dataArr = dataObj.optJSONArray("data")
            Log.d("Test", "credit card array:" + dataArr)

            val size = dataArr.length()
            for (i in 0..size - 1) {

                val item = dataArr.getJSONObject(i)

                val creditcard_id = item.getString("creditcard_id")
                val last_four     = item.getString("last_four")
                val cardname      = item.getString("cardname")
                val cardyear      = item.getInt("cardyear")
                val cardmonth     = item.getInt("cardmonth")
                val cardtype      = item.getInt("cardtype")
                val cardtype_name = item.getString("cardtype_name")
                val is_default    = item.getInt("is_default")

                val creditCard_item =  CreditCardData(creditcard_id, cardname, cardyear, cardmonth, cardtype, cardtype_name, last_four, is_default)
                creditCard_items.add(creditCard_item)
            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return creditCard_items
    }

    fun checkShippingCardParser(data: String): Array<String?> {

        val array = arrayOfNulls<String>(2)
        Arrays.fill(array, "")

        try {
            Log.d("Test", "check shipping card parser:" + data)
            val dataObj = JSONObject(data)
            //val is_shipping = dataObj.getBoolean("is_shipping")
            //val is_payment = dataObj.getBoolean("is_payment")
            val shipping_id = dataObj.getString("shipping_id", "")
            val creditcard_id = dataObj.getString("creditcard_id", "")

            array[0] = shipping_id
            array[1] = creditcard_id

        } catch (ex: JSONException){
            ex.printStackTrace()
        }

        return array
    }

    fun tempCartCountParser(data: String): Count {

        var counts = Count()

        try {

            val obj = JSONObject(data)
            Log.d("Test", " === temp count:" + obj)
            val dataObj = obj.getJSONObject("data")
            val cartObj = dataObj.getJSONObject("temp_cart")

            val cart_count = cartObj.getInt("count")
            val total_price = cartObj.getDouble("total_price")
            val total_final_price = cartObj.getDouble("total_final_price")
            val currency = cartObj.getInt("currency")
            val total_discount = cartObj.getDouble("total_discount")

            counts = Count(cart_count = cart_count, total_price = total_price, total_final_price = total_final_price, currency = currency, total_discount = total_discount)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return counts
    }

    fun liveCartCountParser(data: String): Count {

        var counts = Count()

        try {

            val obj = JSONObject(data)
            Log.d("Test", " === live count:" + obj)
            val dataObj = obj.getJSONObject("data")
            val cartObj = dataObj.getJSONObject("cart")

            val cart_count = cartObj.getInt("count")
            val total_price = cartObj.getDouble("total_price")
            val total_final_price = cartObj.getDouble("total_final_price")
            val currency = cartObj.getInt("currency")
            val total_discount = cartObj.getDouble("total_discount")

            counts = Count(cart_count = cart_count, total_price = total_price, total_final_price = total_final_price, currency = currency, total_discount = total_discount)

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return counts
    }
}