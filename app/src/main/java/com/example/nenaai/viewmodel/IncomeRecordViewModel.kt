package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.CreateIncomeRecordRequest
import com.example.nenaai.data.model.IncomeRecordResponse
import com.example.nenaai.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeRecordViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _addIncomeRecordEvent = MutableSharedFlow<AddIncomeRecordEvent>()
    val addIncomeRecordEvent: SharedFlow<AddIncomeRecordEvent> = _addIncomeRecordEvent

    fun createIncomeRecord(amount: Double, recordDate: String, notes: String?) {
        viewModelScope.launch {
            val request = CreateIncomeRecordRequest(amount, recordDate, notes)
            val token = tokenManager.getToken()

            if (token.isNullOrEmpty()) {
                _addIncomeRecordEvent.emit(AddIncomeRecordEvent.Error("Authentication token missing."))
                return@launch
            }

            val result = loanRepository.createIncomeRecord(request)
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    _addIncomeRecordEvent.emit(AddIncomeRecordEvent.Success(it))
                } ?: _addIncomeRecordEvent.emit(AddIncomeRecordEvent.Error("Empty response from server."))
            } else {
                _addIncomeRecordEvent.emit(AddIncomeRecordEvent.Error(result.exceptionOrNull()?.message ?: "Unknown error occurred."))
            }
        }
    }
}

sealed class AddIncomeRecordEvent {
    data class Success(val incomeRecord: IncomeRecordResponse) : AddIncomeRecordEvent()
    data class Error(val message: String) : AddIncomeRecordEvent()
}