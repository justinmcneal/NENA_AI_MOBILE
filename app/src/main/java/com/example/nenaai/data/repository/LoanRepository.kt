package com.example.nenaai.data.repository

import com.example.nenaai.data.model.ApplyLoanRequest
import com.example.nenaai.data.model.LoanDetailsResponse
import com.example.nenaai.data.model.LoanResponse
import com.example.nenaai.data.model.LoanStatusResponse
import com.example.nenaai.data.network.ApiService
import javax.inject.Inject

class LoanRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun applyLoan(monthlyIncome: Double, loanedAmount: Double, loanTerm: Int): LoanResponse? {
        val request = ApplyLoanRequest(
            monthly_income = monthlyIncome,
            loaned_amount = loanedAmount,
            loan_term = loanTerm
        )
        val response = apiService.applyLoan(request)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error applying loan: ${response.errorBody()?.string()}")
        }
    }

    suspend fun checkLoanStatus(token: String): Result<LoanStatusResponse> {
        return try {
            val response = apiService.checkLoanStatus("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchLoanDetails(token: String): Result<LoanDetailsResponse> {
        return try {
            val response = apiService.fetchLoanDetails("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}