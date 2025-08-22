package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.model.LoanResponse
import com.example.nenaai.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyLoanViewModel @Inject constructor(private val repository: LoanRepository) : ViewModel() {

    private val _loanResponse = MutableStateFlow<LoanResponse?>(null)
    val loanResponse: StateFlow<LoanResponse?> = _loanResponse

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun applyLoan(monthlyIncome: Double, loanedAmount: Double, loanTerm: Int) {
        viewModelScope.launch {
            try {
                val result = repository.applyLoan(monthlyIncome, loanedAmount, loanTerm)
                _loanResponse.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

