package com.example.firebase_event

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.firebase_event.daos.NewsDao
import com.example.firebase_event.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var newsDao: NewsDao
    private lateinit var documentId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsDao = NewsDao()

        documentId = intent.getStringExtra("documentId")!!
        val headline = intent.getStringExtra("headline")
        val description = intent.getStringExtra("description")
        val category = intent.getStringExtra("category")
        val imageUrl = intent.getStringExtra("imageUrl")

        Toast.makeText(this, documentId, Toast.LENGTH_LONG).show()


        binding.iHeadline.setText(headline)
        binding.iDesc.setText(description)
        binding.iHeadline.setText(headline)
        binding.iCategory.setText(category)
        Glide.with(this).load(imageUrl).into(binding.iImage)



        binding.update.setOnClickListener {

            val uHeadline = binding.iHeadline.text.toString().trim()
            val uDescription = binding.iDesc.text.toString().trim()
            val uCategory = binding.iCategory.text.toString().trim()

            val updates =  mapOf(
                "headline" to uHeadline,
                "description" to uDescription,
                "category" to uCategory
            )

            newsDao.newsCollection.document(documentId).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Updated Successfully", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Update", Toast.LENGTH_LONG).show()
                    finish()
                }

            }
        }
    }



