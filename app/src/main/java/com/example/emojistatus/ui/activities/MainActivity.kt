package com.example.emojistatus.ui.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emojistatus.R
import com.example.emojistatus.adapters.UserListAdapter
import com.example.emojistatus.firestore.FirestoreClass
import com.example.emojistatus.models.User
import com.example.emojistatus.widgets.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    var mUserEmail = ""
    var mUserName = ""
    private lateinit var mUser: User
    private lateinit var mUserList: ArrayList<User>
    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogout: Button
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var mAdapter: UserListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        usersRecyclerView = findViewById(R.id.rvUsers)


        if(intent.hasExtra(Constants.USER_NAME_KEY)){
            mUserName = intent.getStringExtra(Constants.USER_NAME_KEY)!!
            mUserEmail = intent.getStringExtra(Constants.USER_EMAIL_KEY)!!
            getUser()
            getAllUser()
        }
    }


    private companion object {
        private const val TAG = "MainActivity"
        private val VALID_CHAR_TYPES = listOf(
            Character.NON_SPACING_MARK, // 6
            Character.DECIMAL_DIGIT_NUMBER, // 9
            Character.LETTER_NUMBER, // 10
            Character.OTHER_NUMBER, // 11
            Character.SPACE_SEPARATOR, // 12
            Character.FORMAT, // 16
            Character.SURROGATE, // 19
            Character.DASH_PUNCTUATION, // 20
            Character.START_PUNCTUATION, // 21
            Character.END_PUNCTUATION, // 22
            Character.CONNECTOR_PUNCTUATION, // 23
            Character.OTHER_PUNCTUATION, // 24
            Character.MATH_SYMBOL, // 25
            Character.CURRENCY_SYMBOL, //26
            Character.MODIFIER_SYMBOL, // 27
            Character.OTHER_SYMBOL // 28
        ).map { it.toInt() }.toSet()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miLogout) {
            Log.i("MYLOGS:", "Logout")
            auth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        } else if (item.itemId == R.id.miEdit) {
            showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val editText = EditText(this)
        val emojiFilter = EmojiFilter()
        val lengthFilter = InputFilter.LengthFilter(9)
        editText.filters = arrayOf(lengthFilter, emojiFilter)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Update your emojis")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null)
            .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            Log.i("MYLOGS:", "Clicked on positive button!")
            val emojisEntered = editText.text.toString()
            if (emojisEntered.isBlank()) {
                Toast.makeText(this, "Cannot submit empty text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "No signed in user", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirestoreClass().updateEmoji(mUser.id!!, emojisEntered)
            getAllUser()
            dialog.dismiss()
        }
    }
    inner class EmojiFilter : InputFilter {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
            if (source == null || source.isBlank()) {
                return ""
            }
            Log.i("MYLOGS:", "Added text $source has length of ${source.length} characters")
            for (inputChar in source) {
                val type = Character.getType(inputChar)
                Log.i("MYLOGS:", "Character type $type")
                if (!VALID_CHAR_TYPES.contains(type)) {
                    Toast.makeText(this@MainActivity, "Only emojis are allowed", Toast.LENGTH_SHORT).show()
                    return ""
                }
            }
            // The CharSequence being added is a valid emoji! Allow it to be added
            return source
        }
    }

    fun uploadUser(){
        val user = User(
            email= mUserEmail,
            displayName = mUserName,
            emojis = "",
        )
        FirestoreClass().uploadUser(this,user)
    }
    fun getAllUser(){
        FirestoreClass().getAllUser(this)
    }
    fun getUser(){
        FirestoreClass().getUser(this,mUserEmail)
    }
    fun userGetSuccess(user: User){
        mUser = user
    }
    fun successGetAllUser(users: ArrayList<User>){
        if(users.size > 0){
            mUserList = users
            mAdapter = UserListAdapter(this, mUserList, this)
            usersRecyclerView.layoutManager = LinearLayoutManager(this)
            usersRecyclerView.adapter = mAdapter
        }
    }
    fun successUploadUser(){
    }
}