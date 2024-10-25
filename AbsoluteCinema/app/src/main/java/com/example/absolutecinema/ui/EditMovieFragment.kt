package com.example.absolutecinema.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.absolutecinema.R
import com.example.absolutecinema.data.Movie
import com.example.absolutecinema.data.DatabaseHelper

class EditMovieFragment : Fragment() {

    private lateinit var etMovieName: EditText
    private lateinit var etMovieDescription: EditText
    private lateinit var etMovieReleaseDate: EditText
    private lateinit var etMovieType: EditText
    private lateinit var btnAddCover: Button
    private lateinit var btnSaveMovie: Button
    private lateinit var imgCover: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var databaseHelper: DatabaseHelper
    private var movieId: Int = -1

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imgCover.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        etMovieName = view.findViewById(R.id.et_movie_name)
        etMovieDescription = view.findViewById(R.id.et_movie_description)
        etMovieReleaseDate = view.findViewById(R.id.et_movie_release_date)
        etMovieType = view.findViewById(R.id.et_movie_type)
        btnAddCover = view.findViewById(R.id.btn_add_cover)
        btnSaveMovie = view.findViewById(R.id.btn_save_movie)
        imgCover = view.findViewById(R.id.et_img_cover)

        movieId = arguments?.getInt("movieId", -1) ?: -1

        if (movieId != -1) {
            val movie = databaseHelper.getMovieById(movieId)
            movie?.let {
                etMovieName.setText(it.name)
                etMovieDescription.setText(it.description)
                etMovieReleaseDate.setText(it.releaseDate)
                etMovieType.setText(it.type)
                selectedImageUri = Uri.parse(it.coverImageUri)
                imgCover.setImageURI(selectedImageUri)
            }
        }

        btnAddCover.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSaveMovie.setOnClickListener {
            val movie = Movie(
                id = movieId,
                name = etMovieName.text.toString(),
                description = etMovieDescription.text.toString(),
                releaseDate = etMovieReleaseDate.text.toString(),
                type = etMovieType.text.toString(),
                coverImageUri = selectedImageUri.toString()
            )

            if (movieId == -1) {
                databaseHelper.addMovie(movie)
                Toast.makeText(requireContext(), "Filme adicionado", Toast.LENGTH_SHORT).show()
            } else {
                databaseHelper.updateMovie(movie)
                Toast.makeText(requireContext(), "Filme atualizado", Toast.LENGTH_SHORT).show()
            }

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
