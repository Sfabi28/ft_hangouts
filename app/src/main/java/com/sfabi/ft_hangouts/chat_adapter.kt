package com.sfabi.ft_hangouts

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val context: Context, private var originalChatList: List<ChatPreview>) : BaseAdapter(), Filterable {

    private var filteredChatList: List<ChatPreview> = originalChatList

    override fun getCount(): Int = filteredChatList.size

    override fun getItem(position: Int): Any = filteredChatList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)

        val chat = filteredChatList[position]

        val tvName = view.findViewById<TextView>(R.id.tvContactName)
        val tvMessage = view.findViewById<TextView>(R.id.tvLastMessage)
        val tvTime = view.findViewById<TextView>(R.id.tvTimestamp)
        val imgAvatar = view.findViewById<ImageView>(R.id.imgAvatar)
        val tvUnreadBadge = view.findViewById<TextView>(R.id.tvUnreadBadge)

        tvName.text = chat.contactName

        if (chat.lastMessage.isEmpty()) {
            tvMessage.text = context.getString(R.string.new_chat_text)
        } else {
            tvMessage.text = chat.lastMessage
        }

        if (chat.unreadCount > 0) {
            tvUnreadBadge.visibility = View.VISIBLE
            tvUnreadBadge.text = chat.unreadCount.toString()
            tvName.setTypeface(null, android.graphics.Typeface.BOLD)

            if (chat.lastMessage.isEmpty()) {
                tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC)
            } else {
                tvMessage.setTypeface(null, android.graphics.Typeface.BOLD)
            }
        } else {
            tvUnreadBadge.visibility = View.GONE
            tvName.setTypeface(null, android.graphics.Typeface.NORMAL)

            if (chat.lastMessage.isEmpty()) {
                tvMessage.setTypeface(null, android.graphics.Typeface.ITALIC)
            } else {
                tvMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }

        if (chat.timestamp.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val netDate = Date(chat.timestamp.toLong())
                tvTime.text = sdf.format(netDate)
            } catch (e: Exception) {
                tvTime.text = ""
            }
        } else {
            tvTime.text = ""
        }

        if (chat.imageUri != null && chat.imageUri.isNotEmpty()) {
            try {
                val file = java.io.File(chat.imageUri)
                if (file.exists()) {
                    imgAvatar.setImageURI(Uri.fromFile(file))
                } else {
                    imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            } catch (e: Exception) {
                imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        } else {
            imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.toLowerCase(Locale.ROOT) ?: ""
                val results = FilterResults()
                if (charString.isEmpty()) {
                    results.values = originalChatList
                } else {
                    val filtered = originalChatList.filter {
                        it.contactName.toLowerCase(Locale.ROOT).contains(charString)
                    }
                    results.values = filtered
                }
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredChatList = results?.values as? List<ChatPreview> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}
