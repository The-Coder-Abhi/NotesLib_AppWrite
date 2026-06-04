package com.muktasapp.noteslibapp.presentation.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.muktasapp.noteslibapp.R
import com.muktasapp.noteslibapp.Result
import com.muktasapp.noteslibapp.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController,authViewModel: AuthViewModel) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        var enteredEmail by remember { mutableStateOf("") }
        var enteredPass by remember { mutableStateOf("") }

        val signInState by authViewModel.signInState.collectAsState()
        val context = LocalContext.current

        /*Image(
            painter = painterResource(R.drawable.notes_lib),
            contentDescription = "Logo",
            Modifier.size(300.dp,300.dp)
        )*/
        Text(text = "Enter Email:",
            modifier = Modifier.padding(20.dp,0.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.Primary_color),
            fontFamily = FontFamily(Font(R.font.antic_regular))
        )
        TextField(value =enteredEmail,
            onValueChange = {enteredEmail=it},
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            placeholder = {
                Text("eg. myemail@example.com")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = colorResource(R.color.Primary_color)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                focusedTextColor = colorResource(id = R.color.Primary_color)
            )

            )
        Text(text = "Password:",
            modifier = Modifier.padding(20.dp,0.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.Primary_color),
            fontFamily = FontFamily(Font(R.font.antic_regular))
        )
        TextField(value =enteredPass,
            onValueChange = {enteredPass=it},
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock,
                    contentDescription = "Email",
                    tint = colorResource(R.color.Primary_color)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                focusedTextColor = colorResource(id = R.color.Primary_color)
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Box(modifier = Modifier.fillMaxWidth().padding(0.dp,15.dp), contentAlignment = Alignment.Center){
            Button(onClick = {signIn(enteredEmail,enteredPass,context,authViewModel)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.Primary_color)
                ),
                modifier = Modifier.size(300.dp,48.dp)) {
                Text(text="Login",
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.allura_regular)),
                            color = colorResource(R.color.white))

            }

        }
        signInState?.let { result ->
            when (result) {
                is Result.Success -> {
                    LaunchedEffect(result) {
                        // Check verification status before navigating
                        authViewModel.checkVerificationStatus { isVerified ->
                            val route = if (isVerified) "homescreen" else "verifyscreen"
                            navController.navigate(route) {
                                popUpTo("loginscreen") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    LaunchedEffect(result) {
                        Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetSignInState() // Clear the error so it doesn't re-trigger
                    }
                }
            }
        }
        Row(modifier= Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text="Create new one ?",
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.allura_regular)),
                    color=colorResource(R.color.Primary_color))
            TextButton(onClick ={navController.navigate("signupscreen")}, modifier = Modifier.padding(0.dp) ) {
                Text("Sign Up",
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.allura_regular)),
                    color=colorResource(R.color.Primary_color))
            }

        }
        //BasicTextField(value ="", onValueChange = String -> (unit) )

    }
}

fun signIn(email: String, pass: String, context: Context, authViewModel: AuthViewModel) {
    if (email.isEmpty()) {
        Toast.makeText(context, "Pls enter an Email", Toast.LENGTH_SHORT).show()
    } else if (pass.isEmpty()) {
        Toast.makeText(context, "Pls enter a Password", Toast.LENGTH_SHORT).show()
    } else {
        authViewModel.signIn(email, pass)
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen(){
    LoginScreen()
}*/