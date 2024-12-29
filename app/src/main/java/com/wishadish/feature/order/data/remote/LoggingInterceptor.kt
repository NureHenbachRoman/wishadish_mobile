package com.wishadish.feature.order.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Log request details
        println("Outgoing Request:")
        println("Method: ${request.method}")
        println("URL: ${request.url}")
        println("Headers: ${request.headers}")

        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            println("Request Body: ${buffer.readUtf8()}")
        }

        return chain.proceed(request) // Proceed with the request
    }
}
