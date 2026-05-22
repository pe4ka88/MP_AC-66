package com.example.memorygame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MemoryAdapter(
    private val cards: List<MemoryCard>,
    private val cardClickListener: (Int) -> Unit
) : RecyclerView.Adapter<MemoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivCard: ImageView = itemView.findViewById(R.id.ivCard)

        init {
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    cardClickListener(pos)
                }
            }
        }

        fun bind(card: MemoryCard) {
            if (card.isMatched) {
                itemView.visibility = View.INVISIBLE
                itemView.isEnabled = false
                return
            }

            itemView.visibility = View.VISIBLE
            itemView.isEnabled = true

            if (card.isFaceUp) {
                ivCard.setImageResource(card.identifier)
            } else {
                ivCard.setImageResource(R.drawable.ic_android_black_24dp)
            }
        }
    }
}
