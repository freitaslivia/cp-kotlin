package com.example.absolutecinema.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.absolutecinema.R
import com.example.absolutecinema.data.Movie
import com.example.absolutecinema.data.DatabaseHelper

class AddMovieFragment : Fragment() {

    private lateinit var etMovieName: EditText
    private lateinit var etMovieDescription: EditText
    private lateinit var etMovieReleaseDate: EditText
    private lateinit var etMovieType: EditText
    private lateinit var btnAddCover: Button
    private lateinit var btnSaveMovie: Button
    private lateinit var imgCover: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var getContent: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_movie, container, false)
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
        imgCover = view.findViewById(R.id.img_cover)

        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                imgCover.setImageURI(selectedImageUri)
            }
        }

        etMovieReleaseDate.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFormatting) return

                isFormatting = true
                val cleaned = s.toString().replace("/", "")
                val formatted = StringBuilder()

                if (cleaned.length >= 2) {
                    formatted.append(cleaned.substring(0, 2))
                    if (cleaned.length > 2) {
                        formatted.append("/")
                        if (cleaned.length >= 4) {
                            formatted.append(cleaned.substring(2, 4))
                            if (cleaned.length > 4) {
                                formatted.append("/")
                                formatted.append(cleaned.substring(4, cleaned.length.coerceAtMost(8)))
                            }
                        } else {
                            formatted.append(cleaned.substring(2))
                        }
                    }
                } else {
                    formatted.append(cleaned)
                }

                if (formatted.length > 10) {
                    formatted.setLength(10)
                }

                etMovieReleaseDate.setText(formatted.toString())
                etMovieReleaseDate.setSelection(formatted.length)
                isFormatting = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnAddCover.setOnClickListener {
            selectImageFromGallery()
        }

        btnSaveMovie.setOnClickListener {
            saveMovie()
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    private fun saveMovie() {
        val name = etMovieName.text.toString()
        val description = etMovieDescription.text.toString()
        val releaseDate = etMovieReleaseDate.text.toString()
        val type = etMovieType.text.toString()
        val coverImageUri = selectedImageUri.toString()

        if (name.isNotEmpty() && description.isNotEmpty() && releaseDate.isNotEmpty() && type.isNotEmpty()) {
            val movie = Movie(name = name, description = description, releaseDate = releaseDate, type = type, coverImageUri = coverImageUri)
            val result = databaseHelper.addMovie(movie)

            if (result > 0) {
                Toast.makeText(requireContext(), "Filme adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                clearFields()
                navigateToMoviesToWatchFragment()
            } else {
                Toast.makeText(requireContext(), "Erro ao adicionar filme!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        etMovieName.text.clear()
        etMovieDescription.text.clear()
        etMovieReleaseDate.text.clear()
        etMovieType.text.clear()
        imgCover.setImageURI(null)
    }

    private fun navigateToMoviesToWatchFragment() {
        val fragment = MoviesToWatchFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
