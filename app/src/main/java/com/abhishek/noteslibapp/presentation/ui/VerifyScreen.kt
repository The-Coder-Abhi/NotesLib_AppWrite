package com.abhishek.noteslibapp.presentation.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abhishek.noteslibapp.R
import com.abhishek.noteslibapp.presentation.viewmodel.AuthViewModel

@Composable
fun VerifyScreen(authviewModel: AuthViewModel, navController: NavController){
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "Verify",
            modifier = Modifier.padding(0.dp,15.dp,0.dp,15.dp),
            fontFamily = FontFamily(Font(R.font.allura_regular)),
            fontSize = 64.sp,
            color = colorResource(R.color.Primary_color))

        Text(text = "The verification email is sent to your account please click on it to verify email.",
            fontFamily = FontFamily(Font(R.font.antic_regular)),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(0.dp,15.dp,0.dp,15.dp),
            color = colorResource(R.color.LightText_color))

        Button(onClick = { Verify(authviewModel, context, navController) },
            modifier = Modifier.padding(0.dp,15.dp,0.dp,15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.Primary_color)
            )
        ) {
            Text(text = "Done",
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.allura_regular)),
                color = colorResource(R.color.white))
        }
    }
}

fun Verify(authviewModel: AuthViewModel, context: Context, navController: NavController){
    // Call the Appwrite verification check function
    authviewModel.checkVerificationStatus { isVerified ->
        if (isVerified) {
            // Navigate to Home
            navController.navigate("homescreen") {
                popUpTo("verifyscreen") { inclusive = true }
            }
            Toast.makeText(context, "Verified successfully!", Toast.LENGTH_SHORT).show()
        } else {
            // Show "verify email" screen
            Toast.makeText(context, "Please verify your email before proceeding", Toast.LENGTH_SHORT).show()
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewVerifyScreen(){
    VerifyScreen()
}*/