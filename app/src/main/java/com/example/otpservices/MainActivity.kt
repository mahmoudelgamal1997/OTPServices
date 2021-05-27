package com.example.otpservices

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {
    lateinit var auth :FirebaseAuth
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var storedVerificationId = ""
    var resendToken=""
     lateinit var code:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage()

        var editText=findViewById<EditText>(R.id.edittext)
        var button=findViewById<Button>(R.id.button)
        var sendcode =findViewById<Button>(R.id.sendcode)

        sendcode.setOnClickListener {
            initPhoneAuth()
        }

        button.setOnClickListener {
            code = editText.text.toString();
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        }

    }

    fun initPhoneAuth(){
        getCallback()
         val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("" +
                    "+201063030622")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

   fun getCallback(){

      callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

         override fun onVerificationCompleted(credential: PhoneAuthCredential) {
             // This callback will be invoked in two situations:
             // 1 - Instant verification. In some cases the phone number can be instantly
             //     verified without needing to send or enter a verification code.
             // 2 - Auto-retrieval. On some devices Google Play services can automatically
             //     detect the incoming verification SMS and perform verification without
             //     user action.
             Log.d(TAG, "onVerificationCompleted:$credential")
             signInWithPhoneAuthCredential(credential)
         }

         override fun onVerificationFailed(e: FirebaseException) {
             // This callback is invoked in an invalid request for verification is made,
             // for instance if the the phone number format is not valid.
             Log.w(TAG, "onVerificationFailed", e)

             if (e is FirebaseAuthInvalidCredentialsException) {
                 // Invalid request
             } else if (e is FirebaseTooManyRequestsException) {
                 // The SMS quota for the project has been exceeded
             }

             // Show a message and update the UI
         }

         override fun onCodeSent(
             verificationId: String,
             token: PhoneAuthProvider.ForceResendingToken
         ) {
             // The SMS verification code has been sent to the provided phone number, we
             // now need to ask the user to enter the code and then construct a credential
             // by combining the code with a verification ID.
             Log.d(TAG, "onCodeSent:$verificationId")

             // Save verification ID and resending token so we can use them later
             storedVerificationId = verificationId
             resendToken = token.toString()


         } } }

   private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}