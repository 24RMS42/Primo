package com.primo.network.new_models

import com.primo.utils.base.BaseItem

data class RejectItem(var productId: String = "",
                      var productName: String = "",
                      var imageUrl: String = "",
                      var thumbnailUrl: String = "",
                      var errorCode: Int = -1) : BaseItem {

    override fun getBaseImage() = imageUrl

    override fun getBaseName() = productName

    override fun getBaseDescription() = ""

    override fun getBasePrice() = -1.0

    override fun getBaseCurrency() = -1

    override fun getBaseStatus() = errorCode

    override fun getBaseQuantity() = -1

    override fun getStockColor(): String = ""

    override fun getStockSize(): String = ""

    override fun getStockCustom(): String = ""

    override fun getMerchantName(): String = ""

    override fun getMerchantCountry(): String = ""

    override fun getMerchantUrl(): String = ""
}