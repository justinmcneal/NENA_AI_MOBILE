package com.example.nenaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nenaai.data.local.TokenManager
import com.example.nenaai.data.model.IncomeRecordResponse
import com.example.nenaai.data.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeRecordListViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _incomeRecords = MutableStateFlow<List<IncomeRecordResponse>>(emptyList())
    val incomeRecords: StateFlow<List<IncomeRecordResponse>> = _incomeRecords

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchIncomeRecords()
    }

    fun fetchIncomeRecords() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val token = tokenManager.getToken()

            if (token.isNullOrEmpty()) {
                _error.value = "Authentication token missing."
                _loading.value = false
                return
            }

            val result = loanRepository.getIncomeRecords(token)
            if (result.isSuccess) {
                _incomeRecords.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Unknown error occurred."
            }
            _loading.value = false
        }
    }
}