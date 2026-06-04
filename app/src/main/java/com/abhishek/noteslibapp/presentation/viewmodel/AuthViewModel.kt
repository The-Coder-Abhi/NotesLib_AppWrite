package com.abhishek.noteslibapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.noteslibapp.Result
import com.abhishek.noteslibapp.data.model.Users
import com.abhishek.noteslibapp.data.repository.AuthRepository
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val authRepo = AuthRepository()

    private val _userData = MutableStateFlow<Users?>(null)
    val userData: StateFlow<Users?> = _userData

    private val _signInState = MutableStateFlow<Result<Boolean>?>(null)
    val signInState: StateFlow<Result<Boolean>?> = _signInState

    private val _signUpState = MutableStateFlow<Result<Boolean>?>(null)
    val signUpState: StateFlow<Result<Boolean>?> = _signUpState

    fun loadUserData() {
        viewModelScope.launch {
            val appwriterUser = authRepo.getCurrentUser()
            if (appwriterUser != null){
                _userData.value = Users(
                    id = appwriterUser.id,
                    name = appwriterUser.name,
                    email = appwriterUser.email
                )
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _signUpState.value = authRepo.signUp(email, password, name)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            // Calls the Appwrite AuthRepository
            _signInState.value = authRepo.signIn(email, password)
        }
    }

    fun sendEmailVerification(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepo.sendVerificationEmail()
            onResult(result is Result.Success)
        }
    }

    // Since checking verification requires a network call in Appwrite,
    // we use a callback to return the boolean to the UI.
    fun checkVerificationStatus(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isVerified = authRepo.isEmailVerified()
            onResult(isVerified)
        }
    }
    fun checkInitialDestination(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // We ask the repo to fetch the user. If it fails, it will jump to the 'catch' blocks below!
                val user = authRepo.getCurrentUser()

                if (user != null) {
                    if (user.emailVerification) {
                        onResult("homescreen")
                    } else {
                        onResult("verifyscreen")
                    }
                } else {
                    onResult("loginscreen")
                }

            } catch (e: AppwriteException) {
                // THE MAGIC FIX: Appwrite tells us exactly what went wrong!
                if (e.code == 401) {
                    // Error 401 means "Unauthorized". They are truly logged out.
                    onResult("loginscreen")
                } else {
                    // Any other error (like a network timeout) means they likely still have a session!
                    android.util.Log.e("Auth", "Network glitch ignored, routing to home: ${e.message}")
                    onResult("homescreen")
                }
            } catch (e: Exception) {
                // If it crashes for any other reason, play it safe and go to login
                onResult("loginscreen")
            }
        }
    }

    // Helper to clear the state so errors don't pop up twice
    fun resetSignUpState() {
        _signUpState.value = null
    }
    fun resetSignInState() {
        _signInState.value = null
    }
}
