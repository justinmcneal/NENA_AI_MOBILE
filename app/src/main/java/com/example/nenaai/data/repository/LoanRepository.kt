package com.example.nenaai.data.repository

import com.example.nenaai.data.model.ApplyLoanRequest
import com.example.nenaai.data.model.LoanResponse
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
}
