package com.example.firebase_event

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase_event.daos.NewsDao
import com.example.firebase_event.databinding.ActivityMainBinding
import com.example.firebase_event.model.NewsModal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsArrayList : ArrayList<NewsModal>
    private lateinit var newsDao: NewsDao

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.CustomToolBar)

        binding.progressBar.visibility = View.VISIBLE
        newsArrayList = ArrayList()
        newsDao = NewsDao()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val query = newsDao.newsCollection.orderBy("postedAt", Query.Direction.DESCENDING)
        query.addSnapshotListener{ snapshot, exception->

            if (exception != null){
                return@addSnapshotListener
            }
            newsArrayList.clear()

            if (snapshot != null) {
                for (document in snapshot.documents){
                    val newsModal : NewsModal? = document.toObject(NewsModal::class.java)
                    if (newsModal != null){
                        val newsItem = NewsModal(newsModal.headline, newsModal.description,
                        newsModal.imageUrl, newsModal.category, newsModal.postedAt,  document.id)
                        newsArrayList.add(newsItem)

                    }
                }
                val adapter = NewsAdapter(this, newsArrayList, this)
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.adapter = adapter
            }
        }
        binding.addNewsItem.setOnClickListener {
            startActivity(Intent(this, add_news::class.java))
        }
    }

    override fun onItemClick(id: String): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("delete") { dialog, which ->
            deleteItem(id)
        }
        builder.setNegativeButton("cancel") { dialog, which ->
            dialog.dismiss()
        }
        
        val dialog = builder.create()
        dialog.show()

       return true
    }

    private fun deleteItem(id: String) {
        val docRef = newsDao.newsCollection.document(id)

        docRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "successfully deleted!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "error deleting $e", Toast.LENGTH_LONG).show()
            }
    }

}