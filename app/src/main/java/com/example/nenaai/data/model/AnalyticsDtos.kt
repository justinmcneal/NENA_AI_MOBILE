package com.example.nenaai.data.model

data class CreateIncomeRecordRequest(
    val amount: Double,
    val record_date: String, // YYYY-MM-DD
    val notes: String?
)

data class IncomeRecordResponse(
    val id: Int,
    val user: String, // Phone number of the user
    val amount: Double,
    val record_date: String,
    val notes: String?,
    val created_at: String
)

data class UserAnalyticsResponse(
    val total_loan_amount: Double,
    val total_amount_repaid: Double,
    val val_average_monthly_income: Double,
    val business_consistency_score: Double,
    val last_updated: String
)
