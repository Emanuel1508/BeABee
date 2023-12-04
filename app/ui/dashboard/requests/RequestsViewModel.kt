package com.ibm.internship.beabee.ui.dashboard.requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.models.GetRequestResponse
import com.ibm.internship.beabee.domain.usecases.GetRequestsUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ibm.internship.beabee.domain.models.containsFilteringString

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getRequestsUseCase: GetRequestsUseCase
) : BaseViewModel() {
    private val _cards = MutableLiveData<List<GetRequestResponse>>()
    val cards: LiveData<List<GetRequestResponse>> get() = _cards

    private val _navigation = SingleLiveEvent<NavDestination>()
    val navigation: SingleLiveEvent<NavDestination> get() = _navigation

    private val _searchBarRefreshState = MutableLiveData<Boolean>()

    val searchBarRefreshState: LiveData<Boolean> = _searchBarRefreshState

    private val _cardListStatus = MutableLiveData<ListStatus>()
    var cardListStatus: LiveData<ListStatus> = _cardListStatus
    private lateinit var requestsList: List<GetRequestResponse>

    init {
        fetchCards()
    }

    private fun fetchCards() {
        showLoading()
        viewModelScope.launch {
            try {
                when (val response = getRequestsUseCase()) {
                    is UseCaseResponse.Success -> {
                        requestsList = response.body
                        _cards.value = requestsList
                    }

                    is UseCaseResponse.Failure -> showError(ErrorMessage.GENERAL)
                }
            } catch (e: Exception) {
                hideLoading()
                showError(ErrorMessage.GENERAL)
            }
            hideLoading()
        }
    }

    fun onFilteringCards(inputText: String) {
        showLoading()
        if (inputText.isEmpty()) {
            _cards.value = requestsList
        }
        _cards.value = _cards.value?.filter { cardEntity ->
            cardEntity.containsFilteringString(inputText)
        }
        verifyCards()
        hideLoading()
    }

    fun userClickedRetry() {
        fetchCards()
    }

    fun onHelpButtonClick(requestId: String) {
        _navigation.value = NavDestination.RequestDetails(requestId)
    }

    fun onRefresh() {
        _cards.value = requestsList
        _searchBarRefreshState.value = true
    }

    private fun verifyCards() {
        _cardListStatus.value = if (_cards.value?.isEmpty() == true)
            ListStatus.NotPopulated
        else
            ListStatus.IsPopulated
    }

    sealed class NavDestination {
        data class RequestDetails(val requestId: String) : NavDestination()
    }

    sealed class ListStatus {
        object IsPopulated : ListStatus()
        object NotPopulated : ListStatus()
    }
}