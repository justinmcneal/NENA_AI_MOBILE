package com.example.nenaai.data.model

data class ApplyLoanRequest(
    val loaned_amount: Double,
    val loan_term: Int,        // in months
    val monthly_income: Double // user's monthly income
)

data class LoanResponse(
    val id: Int,
    val loan_code: String,
    val loaned_amount: String,
    val amount_payable: String,
    val monthly_repayment: String,
    val months_left: Int,
    val is_verified_by_bank: Boolean,
    val created_at: String
)

data class LoanStatusResponse(
    val message: String,
    val phone_number: String,
    val loan_status: String
)

data class LoanDetail(
    val loan_code: String,
    val loaned_amount: Double,
    val amount_payable: Double,
    val monthly_repayment: Double,
    val months_left: Int,
    val loan_term: Int,
    val status: String,
    val created_at: String
)

data class LoanDetailsResponse(
    val message: String,
    val phone_number: String,
    val loan_status: String,
    val loans: List<LoanDetail>
)

