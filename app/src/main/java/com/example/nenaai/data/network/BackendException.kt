package com.example.nenaai.data.network

import java.io.IOException

class BackendException(message: String, val errorCode: Int? = null, val errorBody: String? = null) : IOException(message)
