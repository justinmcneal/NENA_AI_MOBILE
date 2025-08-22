package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.model.LoanDetailsResponse
import com.example.nenaai.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanStatusViewModel @Inject constructor(
    private val loanRepository: LoanRepository
) : ViewModel() {
    private val _loanStatus = MutableStateFlow<LoanStatusState>(LoanStatusState.Idle)
    val loanStatus: StateFlow<LoanStatusState> = _loanStatus.asStateFlow()

    private val _loanDetails = MutableStateFlow<LoanDetailsState>(LoanDetailsState.Idle)
    val loanDetails: StateFlow<LoanDetailsState> = _loanDetails.asStateFlow()

    sealed class LoanStatusState {
        object Idle : LoanStatusState()
        object Loading : LoanStatusState()
        data class Success(val status: String) : LoanStatusState()
        data class Error(val message: String) : LoanStatusState()
    }

    sealed class LoanDetailsState {
        object Idle : LoanDetailsState()
        object Loading : LoanDetailsState()
        data class Success(val loanDetails: LoanDetailsResponse) : LoanDetailsState()
        data class Error(val message: String) : LoanDetailsState()
    }

    fun checkLoanStatus(token: String) {
        viewModelScope.launch {
            _loanStatus.value = LoanStatusState.Loading
            val result = loanRepository.checkLoanStatus(token)
            if (result.isSuccess) {
                _loanStatus.value = LoanStatusState.Success(result.getOrNull()?.loan_status ?: "NONE")
            } else {
                _loanStatus.value = LoanStatusState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to fetch loan status"
                )
            }
        }
    }

    fun fetchLoanData(token: String) {
        viewModelScope.launch {
            _loanDetails.value = LoanDetailsState.Loading
            val result = loanRepository.fetchLoanDetails(token)
            if (result.isSuccess) {
                result.getOrNull()?.let { details ->
                    _loanDetails.value = LoanDetailsState.Success(details)
                    _loanStatus.value = LoanStatusState.Success(details.loan_status)
                } ?: run {
                    _loanDetails.value = LoanDetailsState.Error("No loan details found")
                    _loanStatus.value = LoanStatusState.Success("NONE")
                }
            } else {
                _loanDetails.value = LoanDetailsState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to fetch loan details"
                )
                _loanStatus.value = LoanStatusState.Success("NONE")
            }
        }
    }
}