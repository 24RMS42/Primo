/**
 * Changes:
 *
 * - Add message when scan 0 stock item
 * - 503 HTTP status handling
 * - Add Deeplink feature
 * - Check Shipping address and Credit Card before checkout
 * - Implement Count api integration
 *
 * 2015 © Primo . All rights reserved.
 */


package com.primo.goods.mvp

import android.util.Log
import android.widget.Toast
import com.primo.R
import com.primo.main.MainActivity
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.new_models.*
import com.primo.utils.consts.*
import com.primo.utils.getAndroidId
import com.primo.utils.getInt
import com.primo.utils.isValidEmail
import org.json.JSONObject
import java.util.*


class GoodsTotalPresenterImpl(view: GoodsTotalView) : GoodsTotalPresenter(view) {

    override fun updateCartItem(stockId: String, cartItemId: String, quantity: String) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cartId = auth.cart_id

        if (token.isEmpty() || cartId.isEmpty()) {

            val updateTempCall: UpdateTempCartItem = UpdateTempCartItemImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {
                    if (result != null)
                        view?.updateProductList(result.products)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            if (!stockId.isEmpty())
                updateTempCall.updateTempCartItem(stockId, cartItemId, quantity)

        } else {

            val updateCall: UpdateCartItem = UpdateCartItemImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {
                    if (result != null)
                        view?.updateProductList(result.products)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            if (!stockId.isEmpty())
                updateCall.updateCartItem(stockId, cartItemId, quantity, token, cartId)
        }
    }

    override fun updateCartItem(product: CartItem, isIncrement: Boolean) {

        val quantity: String

        if (isIncrement)
            quantity = (++product.quantity).toString()
        else
            quantity = (--product.quantity).toString()

        updateCartItem(product.stock.stock_id, product.cartItemId, quantity)
    }

    override fun findProductByKeyword(keyword: String) {

        if (!keyword.isEmpty()) {

            val searchCall: SearchProductByKeyword = SearchProductByKeywordImpl(object : ApiResult<String> {

                override fun onResult(result: String) {
                    Log.d("TEST", result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }
            })

            searchCall.searchProductByKeyword(keyword)
        }
    }

    override fun searchProductById(productId: String) {

        val retrieveProductCall: RetrieveProduct = RetrieveProductImpl(object : ApiResult<Product> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Product) {
                    Log.d("Test", "search product result:" + result)
                    if (result != null) {
                        addProduct(result)
                    }
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("Test", "search error:" + message)

                    when (code) {
                        NOT_FOUND_ERROR ->
                            view?.showToastMessage(MainClass.context.getString(R.string.there_is_no_such_product))
                    }

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    Log.d("Test", "search complete")
                    view?.hideProgress()
                }
        })

        Log.d("Test", "search product by id")
        retrieveProductCall.retrieveProduct(productId)
    }

    override fun addProduct(product: Product) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cartId = auth.cart_id

        if (token.isEmpty() /*|| cartId.isEmpty()*/) {
            Log.d("Test","==== temp cart ====")
            val addTempCall: AddItemToTempCart = AddItemToTempCartImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {

                    Log.d("Test", "additemtempcart api result:" + result)
                    if (result != null) {
                        //addItemToTempCart(product.stocks[0].stock_id, 1.toString())
                        view?.updateProductList(result.products)
                    }
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", "additemtempcart:errormsg:" + message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            if (product.stocks.size > 0) {
                Log.d("Test", "adding item to temp cart")
                addTempCall.addItemToTempCart(product.stocks[0].stock_id, 1.toString())
            }
        } else {
            Log.d("Test","==== cart ====");
            val addCall: AddItemToCart = AddItemToCartImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {

                    if (result != null) {
                        Log.d("Test", "product result:" + result)

                        view?.updateProductList(result.products)

                        if (!result.cartId.isEmpty()) {
                            val auth = MainClass.getAuth()
                            auth.cart_id = result.cartId
                            MainClass.saveAuth(auth)
                        }
                    }
                }

                override fun onError(message: String, code: Int) {
                    Log.d("Test", "add error" + message)
                    view?.showErrorMessage(message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    Log.d("Test", "add complete")
                    view?.hideProgress()
                }
            })

            if (product.stocks.size > 0) {
                Log.d("Test", "adding item to cart")
                addCall.addItemToCart(product.stocks[0].stock_id, 1.toString(), token)
            }
        }

    }

    fun showDialog(message: String){

            val builder = android.support.v7.app.AlertDialog.Builder(MainClass.context, R.style.DialogTheme)
            builder.setMessage(message)

            builder.setPositiveButton(android.R.string.ok, { dialogInterface, i ->
                dialogInterface.dismiss()
            })

            val dialog = builder.create()
            dialog.show()
    }

    override fun removeProduct(product: CartItem) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cartId = auth.cart_id

        if (token.isEmpty() || cartId.isEmpty()) {

            val removeTempCall: RemoveItemFromTempCart = RemoveItemFromTempCartImpl(object : ApiResult<String> {

                override fun onResult(result: String) {
                    Log.d("TEST", result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }
            })

            removeTempCall.removeItemFromTempCart(product.cartItemId)
        } else {

            val removeCall: RemoveItemFromCart = RemoveItemFromCartImpl(object : ApiResult<String> {

                override fun onResult(result: String) {
                    Log.d("TEST", result)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }
            })

            removeCall.removeItemFromCart(product.cartItemId, token, cartId)
        }

    }



    override fun getProductList() {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cartId = auth.cart_id
        Log.d("Test", " == our cart id:" + cartId)

        if (token.isEmpty() /*|| cartId.isEmpty()*/) { //cartID maybe empty if logged in without product

            val tempRetrieveCall: RetrieveTempCartDetail = RetrieveTempCartDetailImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {
                    if (result != null)
                        view?.updateProductList(result.products)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            Log.d("Test", "== retrieving temp cart ===")
            tempRetrieveCall.retrieveTempCartDetail()
        } else {

            val retrieveCall: RetrieveCartDetail = RetrieveCartDetailImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {
                    if (result != null)
                        view?.updateProductList(result.products)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            Log.d("Test", "== retrieving live cart ===")
            retrieveCall.retrieveCartDetail(cartId, token)
        }
    }

    override fun addToWishList(product: CartItem) {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        if (!token.isEmpty()) {

            val addToWish: AddWishlistItem = AddWishlistItemImpl(object : ApiResult<String> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: String) {
                    view?.deleteItem(product)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)

                    displayMessage(message, code)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            addToWish.addWishlistItem(product.productId, product.stock.stock_id, product.quantity.toString(), token)
        }
    }

    override fun retrieveProductStock(product: CartItem) {

        val retrieveStockCall: RetrieveProductStock = RetrieveProductStockImpl(object : ApiResult<Pair<CartItem, ArrayList<Stock>>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: Pair<CartItem, ArrayList<Stock>>) {
                view?.showDescriptionDialog(result)
            }

            override fun onError(message: String, code: Int) {
                view?.showErrorMessage(message)
                Log.d("TEST", message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        retrieveStockCall.retrieveProductStock(product)

    }

    override fun signIn(email: String, password: String) {
        if (!email.isEmpty() && !password.isEmpty() && isValidEmail(email)) {

            val signInCall: SignIn = SignInImpl(object: ApiResult<Auth?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Auth?) {

                    if (result != null) {
                        Log.d("Test", result.toString())
                        view?.onSigned(result)
                    }

                    view?.hideProgress()
                }

                override fun onError(message: String, code: Int) {

                    Log.d("TEST", code.toString())
                    Log.d("TEST", message)

                    view?.showErrorMessage(message)

                    view?.hideProgress()
                    displayMessage(message, code)
                }

            })

            signInCall.signIn(email, password, getAndroidId())

        }
    }

    override fun checkShippingCardBeforeCheckout() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        val checkShippingCardCall: CheckShippingCard = CheckShippingCardImpl(object : ApiResult<Array<String?>> {

            override fun onStart() {
                view?.showProgress()
            }

            override fun onResult(result: Array<String?>) {

                view?.onCheckShippingCardBeforeCheckout(result)
            }

            override fun onError(message: String, code: Int) {
                view?.showErrorMessage(message)

                displayMessage(message, code)
            }

            override fun onComplete() {
                view?.hideProgress()
            }
        })

        checkShippingCardCall.checkShippingCard(token)
    }

    override fun getPublicCount() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        val getPublicCountCall: GetCount = GetPublicCountImpl(object : ApiResult<Count> {

            override fun onStart() {}

            override fun onResult(result: Count) {

                view?.getCountResult(result)
            }

            override fun onError(message: String, code: Int) {
                Log.d("Test", "get public count error:" + message)
                displayMessage(message, code)
            }

            override fun onComplete() {}
        })

        getPublicCountCall.getCount(token)
    }

    override fun getLiveCount() {

        val auth = MainClass.getAuth()
        val token = auth.access_token

        val getLiveCountCall: GetCount = GetLiveCountImpl(object : ApiResult<Count> {

            override fun onStart() {}

            override fun onResult(result: Count) {

                view?.getCountResult(result)
            }

            override fun onError(message: String, code: Int) {
                Log.d("Test", "get live count error:" + message)
                displayMessage(message, code)
            }

            override fun onComplete() {}
        })

        getLiveCountCall.getCount(token)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun displayMessage(message: String, code: Int){

        var codeError = -1
        val jsonObject = JSONObject(message)
        codeError = jsonObject.getInt("error_code", -1)

        view?.displayErrorMessage("", codeError)
    }
}