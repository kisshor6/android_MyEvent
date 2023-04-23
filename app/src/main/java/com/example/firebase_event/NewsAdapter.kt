package com.example.firebase_event

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.firebase_event.databinding.NewsItemViewBinding
import com.example.firebase_event.model.NewsModal

class NewsAdapter(private val context: Context, private var NewsListItem: ArrayList<NewsModal>,
                  private val listener: OnItemClickListener
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    class NewsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var binding : NewsItemViewBinding = NewsItemViewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_item_view, parent, false))
        return view
    }

    override fun getItemCount(): Int {
        return NewsListItem.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = NewsListItem[position]
        Glide.with(context)
            .load(newsItem.imageUrl)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                          isFirstResource: Boolean): Boolean {
                    holder.binding.progressBar.visibility = View.GONE
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                             dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    holder.binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(holder.binding.contentImage)
        holder.binding.headline.text = newsItem.headline
        holder.binding.description.text = newsItem.description
        holder.binding.category.text = newsItem.category

        holder.itemView.setOnLongClickListener {
            listener.onItemClick(newsItem.documentId)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("documentId", newsItem.documentId)
            intent.putExtra("headline", newsItem.headline)
            intent.putExtra("description", newsItem.description)
            intent.putExtra("category", newsItem.category)
            intent.putExtra("imageUrl", newsItem.imageUrl)
            context.startActivity(intent)
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(id: String):Boolean
}
