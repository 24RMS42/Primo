package com.primo.utils.base

interface BaseItem {

    fun getBaseImage(): String

    fun getBaseName(): String

    fun getBaseDescription(): String

    fun getBasePrice(): Double

    fun getBaseCurrency(): Int

    fun getBaseStatus(): Int

    fun getBaseQuantity(): Int

    fun getStockColor(): String

    fun getStockSize(): String

    fun getStockCustom(): String

    fun getMerchantName(): String

    fun getMerchantCountry(): String

    fun getMerchantUrl(): String
}