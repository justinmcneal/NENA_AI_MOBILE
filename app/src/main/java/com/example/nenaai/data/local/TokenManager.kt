package com.example.nenaai.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun deleteToken() {
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()
    }

    // New methods for authentication flow state
    fun saveAuthFlowState(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getAuthFlowState(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun clearAuthFlowState() {
        prefs.edit()
            .remove(KEY_OTP_SENT)
            .remove(KEY_OTP_VERIFIED)
            .remove(KEY_PROFILE_COMPLETE)
            .remove(KEY_PIN_SET)
            .remove(KEY_PHONE_NUMBER) // ADDED THIS LINE
            .apply()
    }

    // ADDED FUNCTIONS FOR PHONE NUMBER
    fun savePhoneNumber(phoneNumber: String) {
        prefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    fun getPhoneNumber(): String? {
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"

        // New keys for authentication flow state
        const val KEY_OTP_SENT = "otp_sent"
        const val KEY_OTP_VERIFIED = "otp_verified"
        const val KEY_PROFILE_COMPLETE = "profile_complete"
        const val KEY_PIN_SET = "pin_set"
        const val KEY_PHONE_NUMBER = "phone_number" // ADDED THIS LINE
    }
}