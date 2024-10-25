package com.example.absolutecinema.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog // Importar AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.absolutecinema.R
import com.example.absolutecinema.adapter.FilmAdapter
import com.example.absolutecinema.data.Movie
import com.example.absolutecinema.data.DatabaseHelper

class MoviesToWatchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emptyView: TextView
    private var movieList = mutableListOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movies_to_watch, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_movies)
        recyclerView.layoutManager = LinearLayoutManager(context)

        emptyView = view.findViewById(R.id.empty_view)

        databaseHelper = DatabaseHelper(requireContext())

        filmAdapter = FilmAdapter(
            movieList,
            onEditClick = { movie -> navigateToEditMovie(movie) },
            onDeleteClick = { movie -> confirmDeleteMovie(movie) // Alterar para chamar o método de confirmação
            }
        )

        recyclerView.adapter = filmAdapter

        loadMovies()

        return view
    }

    private fun loadMovies() {
        movieList.clear()
        movieList.addAll(databaseHelper.getAllMovies())

        if (movieList.isNotEmpty()) {
            filmAdapter.notifyDataSetChanged()
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun navigateToEditMovie(movie: Movie) {
        val bundle = Bundle().apply {
            putInt("movieId", movie.id)
        }
        val fragment = EditMovieFragment()
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun confirmDeleteMovie(movie: Movie) {
        // Criar o diálogo de confirmação
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Exclusão")
            .setMessage("Você tem certeza que deseja excluir '${movie.name}'?")
            .setPositiveButton("Sim") { _, _ ->
                deleteMovie(movie) // Se confirmado, chama o método de exclusão
            }
            .setNegativeButton("Não", null) // Se não, apenas fecha o diálogo
            .create()

        alertDialog.show() // Mostrar o diálogo
    }

    private fun deleteMovie(movie: Movie) {
        databaseHelper.deleteMovie(movie.id)
        loadMovies()
    }
}
