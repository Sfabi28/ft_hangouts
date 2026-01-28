package com.sfabi.ft_hangouts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val context: Context,
    private var messages: List<Message>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val TYPE_SENT = 1
    private val TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        return messages[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutRes = if (viewType == TYPE_SENT) {
            R.layout.item_message_sent
        } else {
            R.layout.item_message_received
        }

        val view = LayoutInflater.from(context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.tvBody.text = message.body
        holder.tvTime.text = formatTime(message.timestamp)

        if (getItemViewType(position) == TYPE_SENT) {
            val colorString = ThemeUtils.getHeaderColor(context)
            val colorInt = try {
                Color.parseColor(colorString)
            } catch (e: Exception) {
                Color.parseColor("#000000")
            }

            val background = holder.tvBody.background.mutate() as GradientDrawable
            background.setColor(colorInt)

            val darkness = 1 - (0.299 * Color.red(colorInt) + 0.587 * Color.green(colorInt) + 0.114 * Color.blue(colorInt)) / 255
            if (darkness < 0.5) {
                holder.tvBody.setTextColor(Color.BLACK)
            } else {
                holder.tvBody.setTextColor(Color.WHITE)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        this.messages = newMessages
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: String): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(timestamp.toLong())
            sdf.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBody: TextView = itemView.findViewById(R.id.tvMessageBody)
        val tvTime: TextView = itemView.findViewById(R.id.tvMessageTime)
    }
}