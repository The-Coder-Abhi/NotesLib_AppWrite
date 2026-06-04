package com.muktasapp.noteslibapp.presentation.ui

import android.content.Context
import android.util.Patterns
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
import androidx.compose.material.icons.filled.AccountBox
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
import com.muktasapp.noteslibapp.presentation.viewmodel.AuthViewModel
import androidx.compose.runtime.LaunchedEffect
// Add this line with your other imports at the top!
import com.muktasapp.noteslibapp.Result



@Composable
fun SignUpScreen(authViewModel: AuthViewModel, navController: NavController){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        val context = LocalContext.current
        var enteredEmail by remember { mutableStateOf("") }
        var enteredName by remember { mutableStateOf("") }
        var enteredPass by remember { mutableStateOf("") }
        var enteredConPass by remember { mutableStateOf("") }

        var isLoading by remember { mutableStateOf(false) }

        // Appwrite State Observer
        val signUpState by authViewModel.signUpState.collectAsState()

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 30.dp), horizontalAlignment = Alignment.CenterHorizontally ){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                Text(text="Create",
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.allura_regular)),
                    color=colorResource(R.color.LightText_color),
                    modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp))
                Text(text="New",
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.antic_regular)),
                    color=colorResource(R.color.LightText_color),
                    modifier = Modifier.padding(0.dp,0.dp,120.dp,0.dp))
            }

            Text(
                text = "Note's Lib",
                fontSize = 52.sp,
                fontFamily = FontFamily(Font(R.font.allura_regular)),
                color = colorResource(R.color.Primary_color),
            )
            Text(text="Account",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.antic_regular)),
                color=colorResource(R.color.LightText_color),
                modifier = Modifier.padding(120.dp,0.dp,0.dp,0.dp))
        }
        Text(text = "Name:",
            modifier = Modifier.padding(20.dp,0.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.Primary_color),
            fontFamily = FontFamily(Font(R.font.antic_regular))
        )
        //Name TextField
        TextField(value =enteredName,
            onValueChange = {enteredName=it},
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            placeholder = { Text("eg. Mark Zuckerberg") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Name", tint = colorResource(R.color.Primary_color))
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                focusedTextColor = colorResource(id = R.color.Primary_color)
            )
        )
        Text(text = "Email:",
            modifier = Modifier.padding(20.dp,0.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.Primary_color),
            fontFamily = FontFamily(Font(R.font.antic_regular))
        )
        //Email TextField
        TextField(value =enteredEmail,
            onValueChange = {enteredEmail=it},
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            placeholder = { Text("eg. myemail@example.com") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = colorResource(R.color.Primary_color))
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
        //Password TextField
        TextField(value =enteredPass,
            onValueChange = {enteredPass=it},
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password", tint = colorResource(R.color.Primary_color))
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                focusedTextColor = colorResource(id = R.color.Primary_color)
            ),
            visualTransformation = PasswordVisualTransformation()
        )
        Text(text = "Confirm Password:",
            modifier = Modifier.padding(20.dp,0.dp),
            fontSize = 16.sp,
            color = colorResource(R.color.Primary_color),
            fontFamily = FontFamily(Font(R.font.antic_regular))
        )
        //Confirm Password TextField
        TextField(value =enteredConPass,
            onValueChange = {enteredConPass=it},
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirm Password", tint = colorResource(R.color.Primary_color))
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedContainerColor = colorResource(id = R.color.Secondary_color),
                unfocusedIndicatorColor = colorResource(id = R.color.Primary_color),
                focusedTextColor = colorResource(id = R.color.Primary_color)
            ),
            visualTransformation = PasswordVisualTransformation()
        )
        //Signup Button
        Box(modifier = Modifier.fillMaxWidth().padding(0.dp, 15.dp), contentAlignment = Alignment.Center){
            Button(onClick = {
                val isValid = SignUp(enteredEmail, enteredPass, enteredName, enteredConPass, context, authViewModel)
                if (isValid){
                    isLoading = true
                }
            },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.Primary_color)),
                modifier = Modifier.size(300.dp,48.dp),enabled = !isLoading) {
                if (isLoading) {
                    // Show a loading circle inside the button!
                    androidx.compose.material3.CircularProgressIndicator(
                        color = colorResource(R.color.white),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text="Sign Up",
                        fontSize = 32.sp,
                        fontFamily = FontFamily(Font(R.font.allura_regular)),
                        color = colorResource(R.color.white))
                }
            }
        }

        // --- APPWRITE STATE HANDLING ---
        signUpState?.let { result ->
            when (result) {
                is Result.Success -> {
                    LaunchedEffect(result) {
                        isLoading = false // Turn off loading

                        // Clear the fields ONLY on success
                        enteredName = ""
                        enteredPass = ""
                        enteredEmail = ""
                        enteredConPass = ""

                        navController.navigate("verifyscreen") {
                            popUpTo("signupscreen") { inclusive = true }
                        }
                        authViewModel.resetSignUpState()
                    }
                }
                is Result.Error -> {
                    LaunchedEffect(result) {
                        isLoading = false // Turn off loading

                        // This Toast will tell you EXACTLY why Appwrite rejected the signup!
                        Toast.makeText(context, "Error: ${result.exception.message}", Toast.LENGTH_LONG).show()
                        authViewModel.resetSignUpState()
                    }
                }
            }
        }

        //Text below button
        Row(modifier= Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text="Already have account ?",
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.allura_regular)),
                color=colorResource(R.color.Primary_color))
            TextButton(onClick ={ navController.navigate("loginscreen") }, modifier = Modifier.padding(0.dp) ) {
                Text("Login in",
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.allura_regular)),
                    color=colorResource(R.color.Primary_color))
            }
        }
    }
}

fun SignUp(email:String, pass:String, name:String, conPass:String, context: Context, authViewModel: AuthViewModel): Boolean{
    if(email.isEmpty()){
        Toast.makeText(context,"Pls enter an Email", Toast.LENGTH_SHORT).show()
        return false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        Toast.makeText(context,"Enter valid email address", Toast.LENGTH_SHORT).show()
        return false
    } else if(conPass != pass){
        Toast.makeText(context,"Password and Confirm Password must be same", Toast.LENGTH_SHORT).show()
        return false
    } else if (pass.length < 8) { // Appwrite strict requirement
        Toast.makeText(context,"Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
        return false
    } else {
        authViewModel.signUp(email, pass, name)
        return true
    }
}


/*
@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen(){
    //val navController = rememberNavController()
    SignUpScreen()
}*/