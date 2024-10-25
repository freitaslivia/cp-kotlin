package com.example.absolutecinema.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.absolutecinema.R
import com.example.absolutecinema.data.Movie

class FilmAdapter(
    private val filmes: List<Movie>,
    private val onEditClick: (Movie) -> Unit,
    private val onDeleteClick: (Movie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MINIMAL = 1
    private val VIEW_TYPE_EXPANDED = 2
    private var expandedPosition: Int = -1

    override fun getItemViewType(position: Int): Int {
        return if (position == expandedPosition) VIEW_TYPE_EXPANDED else VIEW_TYPE_MINIMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MINIMAL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_film_minimal, parent, false)
            MinimalViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_film_expanded, parent, false)
            ExpandedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = filmes[position]

        if (holder is MinimalViewHolder) {
            holder.bind(movie)

            holder.itemView.setOnClickListener {
                expandedPosition = if (expandedPosition == position) -1 else position
                notifyDataSetChanged()
            }
        } else if (holder is ExpandedViewHolder) {
            holder.bind(movie)

            holder.itemView.setOnClickListener {
                expandedPosition = if (expandedPosition == position) -1 else position
                notifyDataSetChanged()
            }

            holder.ivEditFilm.setOnClickListener { onEditClick(movie) }
            holder.ivDeleteFilm.setOnClickListener { onDeleteClick(movie) }
        }
    }

    override fun getItemCount(): Int = filmes.size

    inner class MinimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFilmName: TextView = itemView.findViewById(R.id.tv_film_name)
        private val tvFilmDate: TextView = itemView.findViewById(R.id.tv_film_date)

        fun bind(movie: Movie) {
            tvFilmName.text = movie.name
            tvFilmDate.text = movie.releaseDate
        }
    }

    inner class ExpandedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFilmNameExpanded: TextView = itemView.findViewById(R.id.tv_film_name_expanded)
        private val tvFilmDateExpanded: TextView = itemView.findViewById(R.id.tv_film_date_expanded)
        private val tvFilmDescription: TextView = itemView.findViewById(R.id.tv_film_description)
        private val ivFilmImage: ImageView = itemView.findViewById(R.id.iv_film_image)
        val ivEditFilm: ImageView = itemView.findViewById(R.id.iv_edit_film)
        val ivDeleteFilm: ImageView = itemView.findViewById(R.id.iv_delete_film)

        fun bind(movie: Movie) {
            tvFilmNameExpanded.text = movie.name
            tvFilmDateExpanded.text = movie.releaseDate
            tvFilmDescription.text = movie.description
            Glide.with(itemView.context).load(movie.coverImageUri).into(ivFilmImage)
        }
    }
}
