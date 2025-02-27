package com.example.a2048game.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a2048game.databinding.HistoryItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<GameSession, HistoryAdapter.HistoryViewHolder>(GameSessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val session = getItem(position)
        holder.bind(session)
    }

    class HistoryViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

        fun bind(session: GameSession) {
            binding.tvScore.text = "Score: ${session.score}"
            binding.tvDate.text = "Date: ${formatDate(session.date)}"
        }

        private fun formatDate(dateInMillis: Long): String {
            val date = Date(dateInMillis)
            return dateFormat.format(date)
        }
    }

    class GameSessionDiffCallback : DiffUtil.ItemCallback<GameSession>() {
        override fun areItemsTheSame(oldItem: GameSession, newItem: GameSession): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GameSession, newItem: GameSession): Boolean {
            return oldItem == newItem
        }
    }
}
