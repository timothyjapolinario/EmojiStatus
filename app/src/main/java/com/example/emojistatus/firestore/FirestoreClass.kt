package com.example.emojistatus.firestore

import com.example.emojistatus.models.User
import com.example.emojistatus.ui.activities.MainActivity
import com.example.emojistatus.widgets.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun updateEmoji(userId: String, emojis:String){
        mFirestore.collection(Constants.USERS)
            .document(userId)
            .update("emojis", emojis)
    }

    fun getUser(activity: MainActivity, email: String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {document ->

                if(document.isEmpty){
                    //New user
                    activity.uploadUser()
                }else {
                    var user: User? = null
                    for (i in document) {
                        user = i.toObject(User::class.java)
                    }
                    activity.userGetSuccess(user!!)
                }
            }
    }
    fun getAllUser(activity: MainActivity){
        mFirestore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { document ->
                val userList: ArrayList<User> = ArrayList()
                for(i in document){
                    val user = i.toObject(User::class.java)
                    userList.add(user)
                }
                activity.successGetAllUser(userList)
            }
    }
    fun uploadUser(activity: MainActivity, user: User){
        mFirestore.collection(Constants.USERS)
            .document()
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.successUploadUser()
            }
    }
}