package com.example.emojistatus.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.emojistatus.R
import com.example.emojistatus.models.User
import com.example.emojistatus.ui.activities.MainActivity

open class UserListAdapter(
    private val context: Context,
    private var users: ArrayList<User>,
    private val activity: MainActivity
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_2,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = users[position]
        if (holder is MyViewHolder) {
            val tvName: TextView = holder.itemView.findViewById(android.R.id.text1)
            val tvEmojis: TextView = holder.itemView.findViewById(android.R.id.text2)
            tvName.text = model.displayName
            tvEmojis.text = model.emojis
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}