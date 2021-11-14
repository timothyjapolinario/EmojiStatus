package com.example.emojistatus.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
class User(
    @DocumentId
    var id: String? = null,
    var email: String = "",
    var displayName: String = "",
    var emojis: String = ""
):Parcelable
