package com.github.aayman93.currencyconverter.repository

import com.github.aayman93.currencyconverter.data.models.ConversionResponse
import com.github.aayman93.currencyconverter.data.network.CurrencyApi
import com.github.aayman93.currencyconverter.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val api: CurrencyApi
) {

    suspend fun convert(
        base: String,
        target: String,
        amount: String
    ): Resource<ConversionResponse> {
        return try {
            val response = api.convert(base, target, amount)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}