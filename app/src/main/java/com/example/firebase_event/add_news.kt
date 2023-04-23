package com.example.firebase_event

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.firebase_event.daos.NewsDao
import com.example.firebase_event.databinding.ActivityAddNewsBinding
import com.example.firebase_event.model.NewsModal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class add_news : AppCompatActivity() {
    private lateinit var binding : ActivityAddNewsBinding
    lateinit var category : String
    private lateinit var selectedImage : Uri
    private lateinit var newsDao: NewsDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsDao = NewsDao()

        binding.selectPicture.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        val adapter = ArrayAdapter.createFromResource(this, R.array.options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                category = p0?.getItemAtPosition(p2) as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(applicationContext, "nothing",  Toast.LENGTH_SHORT).show()
            }
        }

        binding.addNewsItem.setOnClickListener {
            val getHeadline = binding.headLine.text.toString()
            val getDesc = binding.description.text.toString()

            if (getHeadline != "" && getDesc != "" && category != "" && selectedImage != null){
                addNews(getHeadline, getDesc, selectedImage, category)
                binding.LoadProgressBar.visibility = View.VISIBLE

            }else{
                Toast.makeText(this, "Please Input All the fields", Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null){
            if (data.data != null){
                selectedImage = data.data!!

                val photoName = getFileNameFromUri(selectedImage)
                binding.picture.text = photoName
            }
        }
    }

    private fun getFileNameFromUri(selectedImage: Uri): String {
        var result = ""
        if (selectedImage.scheme == "content"){
            val cursor = contentResolver.query(selectedImage, null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()){
                    val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result =cursor.getString(column)
                }
                cursor.close()
            }
        }
        else if (selectedImage.scheme == "file"){
            result = selectedImage.lastPathSegment ?: ""
        }
        return result
    }

    private fun addNews(headLine : String, description : String, imageUrl : Uri, category : String){
        GlobalScope.launch {
            newsDao.imageStorageReference.putFile(imageUrl).addOnCompleteListener{
                if (it.isSuccessful){
                    newsDao.imageStorageReference.downloadUrl.addOnSuccessListener { task->
                        val image = task.toString()
                        val currentTime = System.currentTimeMillis()
                        val newsModal = NewsModal(headLine, description, image, category, currentTime)
                        newsDao.newsCollection.document().set(newsModal)
                        binding.LoadProgressBar.visibility = View.GONE
                        Toast.makeText(this@add_news, "Successfully posted", Toast.LENGTH_LONG).show()
                        finish()

                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this@add_news, "Failed to Post post", Toast.LENGTH_LONG).show()
                binding.LoadProgressBar.visibility = View.GONE
            }
        }
    }
}