package com.example.firebase_event.daos

import android.net.Uri
import android.util.Log
import com.example.firebase_event.model.NewsModal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class NewsDao {
    private val db = FirebaseFirestore.getInstance()
     val storage = FirebaseStorage.getInstance()
    val newsCollection = db.collection("newsPost")
     val imageStorageReference = storage.reference.child("contentPicture").child(Date().time.toString())



}