package com.tomoima.logbot.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tomoima.logbot.R
import java.util.*


class LogAdapter(private val bufferSize: Int) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    private val list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_log, parent, false)
        return LogViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val text = list[position]
        holder.logText.text = text
    }

    fun addLog(log: String) {
        list.add(log)
        if (list.size > bufferSize) {
            list.removeAt(0)
            notifyItemRemoved(0)
        }
        notifyItemInserted(list.size - 1)
    }

    fun clearLog() {
        list.clear()
        notifyDataSetChanged()
    }

    fun getFullFormattedLogs(): String = list.joinToString(separator = "\n")

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logText: TextView = itemView.findViewById(R.id.log_text)
    }
}