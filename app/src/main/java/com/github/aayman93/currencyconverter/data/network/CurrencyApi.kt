package com.github.aayman93.currencyconverter.data.network

import com.github.aayman93.currencyconverter.BuildConfig
import com.github.aayman93.currencyconverter.data.models.ConversionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {

    @GET("{api_key}/pair/{base}/{target}/{amount}")
    suspend fun convert(
        @Path("base") base: String,
        @Path("target") target: String,
        @Path("amount") amount: String,
        @Path("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<ConversionResponse>
}