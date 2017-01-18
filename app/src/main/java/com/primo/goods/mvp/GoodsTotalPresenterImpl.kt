package com.primo.goods.mvp

import android.util.Log
import com.primo.R
import com.primo.main.MainClass
import com.primo.network.api_new.*
import com.primo.network.new_models.*
import com.primo.utils.consts.ACCESS_NOT_GRANTED_DATA_IS_NOT_VALID
import com.primo.utils.consts.ACCESS_NOT_GRANTED_USER_NOT_CONFIRMED
import com.primo.utils.consts.CONNECTION_ERROR
import com.primo.utils.consts.NOT_FOUND_ERROR
import com.primo.utils.getAndroidId
import com.primo.utils.isValidEmail
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
                }
            })

            searchCall.searchProductByKeyword(keyword)
        }
    }

    override fun addProduct(product: Product) {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cartId = auth.cart_id

        if (token.isEmpty() || cartId.isEmpty()) {

            val addTempCall: AddItemToTempCart = AddItemToTempCartImpl(object : ApiResult<Cart?> {

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
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            if (product.stocks.size > 0)
                addTempCall.addItemToTempCart(product.stocks[0].stock_id, 1.toString())
        } else {

            val addCall: AddItemToCart = AddItemToCartImpl(object : ApiResult<Cart?> {

                override fun onStart() {
                    view?.showProgress()
                }

                override fun onResult(result: Cart?) {

                    if (result != null) {
                        view?.updateProductList(result.products)

                        if (!result.cartId.isEmpty()) {
                            val auth = MainClass.getAuth()
                            auth.cart_id = result.cartId
                            MainClass.saveAuth(auth)
                        }
                    }
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)
                }

                override fun onComplete() {
                    view?.hideProgress()
                }
            })

            if (product.stocks.size > 0)
                addCall.addItemToCart(product.stocks[0].stock_id, 1.toString(), token)
        }

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
                }
            })

            removeCall.removeItemFromCart(product.cartItemId, token, cartId)
        }

    }



    override fun getProductList() {

        val auth = MainClass.getAuth()
        val token = auth.access_token
        val cartId = auth.cart_id

        if (token.isEmpty() || cartId.isEmpty()) {

            val tempRetrieveCall: RetrieveTempCartDetail = RetrieveTempCartDetailImpl(object : ApiResult<Cart?> {

                override fun onResult(result: Cart?) {
                    if (result != null)
                        view?.updateProductList(result.products)
                }

                override fun onError(message: String, code: Int) {
                    //view?.showErrorMessage(message)
                    Log.d("TEST", message)
                }
            })

            tempRetrieveCall.retrieveTempCartDetail()
        } else {

            val retrieveCall: RetrieveCartDetail = RetrieveCartDetailImpl(object : ApiResult<Cart?> {

                override fun onResult(result: Cart?) {
                    if (result != null)
                        view?.updateProductList(result.products)
                }

                override fun onError(message: String, code: Int) {
                    view?.showErrorMessage(message)
                    Log.d("TEST", message)
                }
            })

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
                }

            })

            signInCall.signIn(email, password, getAndroidId())

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}