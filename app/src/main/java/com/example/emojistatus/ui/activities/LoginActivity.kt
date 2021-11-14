package com.example.emojistatus.ui.activities
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.emojistatus.R
import com.example.emojistatus.widgets.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private companion object LoginActivity {
        private const val TAG = "MYLOGS:"
        private const val RC_GOOGLE_SIGN_IN = 4926
    }
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {result  ->
        Log.i(TAG, result.resultCode.toString())
        if(result.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            Log.i("MYLOGS:", "LOGGED IN!")

            try{
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var client: GoogleSignInClient

    private lateinit var btnSignIn: SignInButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSignIn = findViewById(R.id.btnSignIn)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        client = GoogleSignIn.getClient(this, gso);
        auth = Firebase.auth
        btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    fun signInUser(){
        val signInIntent = client.signInIntent
        startForResult.launch(signInIntent)
    }
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            Log.w(TAG, "user not signed in..")
            return
        }
        Log.i("MYLOGS: ","USER_DISPLAYNAME: ${user.displayName}")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.USER_NAME_KEY, user.displayName.toString())
        intent.putExtra(Constants.USER_EMAIL_KEY, user.email.toString())
        startActivity(intent)
        finish()
        // Navigate to MainActivity
    }
}