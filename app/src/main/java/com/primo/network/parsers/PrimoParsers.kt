package com.primo.network.parsers

import com.primo.network.new_models.*
import com.primo.utils.*
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

            auth = Auth(access_token, expires_in, user_status, cart_id, creditcard_id, shipping_id)

        } catch(ex: JSONException) {
            ex.printStackTrace()
        }

        return auth
    }

    fun userProfileParser(data: String): UserProfile? {

        var userProfile: UserProfile? = null

        try {

            val jsonObject = JSONObject(data)

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
                    state, country, postcode, delivery_preference, is_mail_campaign)

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

            val productId = jsonObject.getString("product_id", "")
            val cartItemId = jsonObject.getString("cart_item_id", "")
            val quantity = jsonObject.getInt("quantity", 1)
            val productName = jsonObject.getString("product_name", "")

            val defaultImageObj = jsonObject.getJSONObject("default_image", JSONObject())
            val imageUrl = defaultImageObj.getString("image_url", "")
            val thumbnailUrl = defaultImageObj.getString("image_thumbnail_url", "")

            val stock = stockParser(jsonObject.getString("stock", "")) ?: Stock()

            val dataSupportObject = jsonObject.getJSONObject("data_support", JSONObject())
            val currency = dataSupportObject.getInt("currency", 0)
            val price = dataSupportObject.getDouble("price", 0.0)
            val description = dataSupportObject.getString("description", "")
            val shippingDomOption = dataSupportObject.getInt("shipping_domestic_option", 0)
            val shippingDomAmount = dataSupportObject.getDouble("shipping_domestic_amount", 0.0)
            val shippingInterOption = dataSupportObject.getInt("shipping_international_option", 0)
            val shippingInterAmount = dataSupportObject.getDouble("shipping_international_amount", 0.0)

            cartItem = CartItem(productId, cartItemId, quantity, productName, imageUrl, thumbnailUrl, stock,
                    price, currency, description, shippingDomOption, shippingDomAmount,
                    shippingInterOption, shippingInterAmount)
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

            val jsonObject = JSONObject(data)

            val quantity = jsonObject.getInt("quantity", 0)
            val wishlistId = jsonObject.getString("wishlist_id", "")

            val productObj = jsonObject.getJSONObject("product", JSONObject())

            val productId = productObj.getString("product_id", "")

            val detailObject = productObj.getJSONObject("detail", JSONObject())

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
                    thumbnailUrl, stock, price, currency, description)
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
            val custom = jsonObj.getString("custom", "")

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

            val unique_id = jsonObj.getString("unique_id", "")
            val cart_id = jsonObj.getString("cart_id", "")

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

                    val item = CartItem(productId, ""/*empty*/, 1, productName, productImageUrl, productImageUrl, stock, price, currency, description, -1, 0.0, -1, 0.0)

                    items.add(item)
                }

            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        return items
    }
}