package com.ibm.internship.beabee.ui.dashboard.myprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ibm.beabee.utils.BeeLog
import com.ibm.internship.beabee.base.BaseViewModel
import com.ibm.internship.beabee.domain.usecases.DeactivateAccountUseCase
import com.ibm.internship.beabee.domain.usecases.UserLogoutUseCase
import com.ibm.internship.beabee.domain.utils.ErrorMessage
import com.ibm.internship.beabee.domain.models.GetUserResponse
import com.ibm.internship.beabee.domain.usecases.GetIdUserLoggedInUseCase
import com.ibm.internship.beabee.domain.usecases.GetUserUseCase
import com.ibm.internship.beabee.domain.utils.UseCaseResponse
import com.ibm.internship.beabee.domain.utils.getInitials
import com.ibm.internship.beabee.utils.Constants.Companion.NUMBER_INITIAL_TAGS
import com.ibm.internship.beabee.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val logoutUseCase: UserLogoutUseCase,
    private val deactivateAccountUseCase: DeactivateAccountUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getIdUserLoggedInUseCase: GetIdUserLoggedInUseCase
) : BaseViewModel() {
    private val _profileData = MutableLiveData(ProfileData())
    val profileData: LiveData<ProfileData> = _profileData

    private val _isSeeMoreVisible = MutableLiveData(false)
    val isSeeMoreVisible: LiveData<Boolean> = _isSeeMoreVisible

    private val _navigation = SingleLiveEvent<ProfileDestination>()
    val navigation: SingleLiveEvent<ProfileDestination> = _navigation

    private val _listTagsState = MutableLiveData<ListTagsState>(ListTagsState.ShowMore)
    val listTagsState: LiveData<ListTagsState> = _listTagsState

    private val listAllTags: ArrayList<String> = ArrayList()
    private val TAG = javaClass.simpleName

    init {
        onGetData()
    }

    fun onGetData() {
        showLoading()
        when (val resultGetId = getIdUserLoggedInUseCase()) {
            is UseCaseResponse.Success -> fetchUser(resultGetId.body)
            is UseCaseResponse.Failure -> showError(resultGetId.error)
        }
    }

    fun onDeactivateAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = deactivateAccountUseCase()) {
                is UseCaseResponse.Success -> setNavigation(ProfileDestination.LoginScreen)
                is UseCaseResponse.Failure -> showError(result.error)
            }
        }
    }

    fun onUpdateCurrentListTags() {
        when (_listTagsState.value) {
            is ListTagsState.ShowLess -> showFirstTags()
            is ListTagsState.ShowMore -> updateCurrentTagList(
                listAllTags,
                ListTagsState.ShowLess
            )

            else -> showError(ErrorMessage.GENERAL)
        }
    }

    fun onLogOut() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = logoutUseCase()) {
                is UseCaseResponse.Success -> setNavigation(ProfileDestination.LoginScreen)
                is UseCaseResponse.Failure -> showError(result.error)
            }
        }
    }
    fun onAccountDetailsClicked() = setNavigation(ProfileDestination.AccountDetails)

    private fun fetchUser(idUser: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultGetUser = getUserUseCase(idUser)) {
                is UseCaseResponse.Success -> setProfileDetails(resultGetUser.body)
                is UseCaseResponse.Failure -> showError(resultGetUser.error)
            }
            hideLoading()
        }
    }

    private fun setProfileDetails(user: GetUserResponse) {
        _profileData.postValue(
            ProfileData(
                fullName = user.name,
                nameInitials = user.name.getInitials(),
                rating = user.ratingAvg,
                numberPeopleHelped = user.nrVotes,
                tags = getInitialTags(user.tags)
            )
        )
        setTags(user.tags)
        BeeLog.d(TAG, "Get user success ${_profileData.value}")
    }

    private fun showFirstTags() =
        updateCurrentTagList(getInitialTags(listAllTags), ListTagsState.ShowMore)

    private fun updateCurrentTagList(tags: List<String>?, listTagsState: ListTagsState) {
        setCurrentTags(tags)
        setListTagsState(listTagsState)
        BeeLog.d(TAG, "List tags:  $tags. \nList tags state: $listTagsState")
    }

    private fun setTags(tags: List<String>?) {
        if (tags != null) {
            listAllTags.addAll(tags)
            setIsSeeMoreVisible(tags)
        }
    }

    private fun setIsSeeMoreVisible(tags: List<String>) =
        _isSeeMoreVisible.postValue(tags.size > NUMBER_INITIAL_TAGS)

    private fun getInitialTags(tags: List<String>?) = tags?.take(NUMBER_INITIAL_TAGS)

    private fun setCurrentTags(tags: List<String>?) =
        _profileData.postValue(_profileData.value?.copy(tags = tags))

    private fun setListTagsState(state: ListTagsState) = _listTagsState.postValue(state)

    private fun setNavigation(destination: ProfileDestination) {
        _navigation.postValue(destination)
        BeeLog.d(TAG, "Navigate to:  $destination")
    }
}