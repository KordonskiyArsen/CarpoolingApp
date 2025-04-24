package com.example.carpooling.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carpooling.R
import com.example.carpooling.data.Comment
import com.example.carpooling.data.User
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import java.io.File
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
class CommentAdapter(
    private val comments: List<Pair<Comment, User>>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.commentAvatar)
        val user: TextView = itemView.findViewById(R.id.commentUser)
        val text: TextView = itemView.findViewById(R.id.commentText)
        val date: TextView = itemView.findViewById(R.id.commentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val (comment, user) = comments[position]
        holder.user.text = user.nickname
        holder.text.text = comment.text
        if (!user.photoUri.isNullOrBlank()) {
            val file = java.io.File(user.photoUri)
            if (file.exists()) {
                Glide.with(holder.avatar.context)
                    .load(file)
                    .apply(RequestOptions.circleCropTransform()) // робить зображення круглим
                    .placeholder(R.drawable.ic_person) // якщо ще не підвантажилося — показує стандартну іконку
                    .into(holder.avatar)
            } else {
                holder.avatar.setImageResource(R.drawable.ic_person)
            }
        } else {
            holder.avatar.setImageResource(R.drawable.ic_person)
        }
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        holder.date.text = sdf.format(Date(comment.timestamp))
    }

    override fun getItemCount(): Int = comments.size
}