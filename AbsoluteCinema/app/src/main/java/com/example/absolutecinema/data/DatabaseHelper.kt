package com.example.absolutecinema.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.absolutecinema.data.Movie

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "movies_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "movies"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_RELEASE_DATE = "release_date"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_COVER_IMAGE_URI = "cover_image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_DESCRIPTION TEXT, "
                + "$COLUMN_RELEASE_DATE TEXT, "
                + "$COLUMN_TYPE TEXT, "
                + "$COLUMN_COVER_IMAGE_URI TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addMovie(movie: Movie): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, movie.name)
            put(COLUMN_DESCRIPTION, movie.description)
            put(COLUMN_RELEASE_DATE, movie.releaseDate)
            put(COLUMN_TYPE, movie.type)
            put(COLUMN_COVER_IMAGE_URI, movie.coverImageUri)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun updateMovie(movie: Movie): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, movie.name)
            put(COLUMN_DESCRIPTION, movie.description)
            put(COLUMN_RELEASE_DATE, movie.releaseDate)
            put(COLUMN_TYPE, movie.type)
            put(COLUMN_COVER_IMAGE_URI, movie.coverImageUri)
        }
        val result = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(movie.id.toString()))
        db.close()
        return result
    }

    fun deleteMovie(movieId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(movieId.toString()))
        db.close()
        return result
    }

    fun getAllMovies(): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching movies", e)
            return emptyList()
        }

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val movie = Movie(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        releaseDate = it.getString(it.getColumnIndexOrThrow(COLUMN_RELEASE_DATE)),
                        type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                        coverImageUri = it.getString(it.getColumnIndexOrThrow(COLUMN_COVER_IMAGE_URI))
                    )
                    movieList.add(movie)
                } while (it.moveToNext())
            }
        }

        db.close()
        return movieList
    }

    fun getMovieById(movieId: Int): Movie? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(movieId.toString()),
            null,
            null,
            null
        )

        var movie: Movie? = null
        cursor?.use {
            if (it.moveToFirst()) {
                movie = Movie(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    releaseDate = it.getString(it.getColumnIndexOrThrow(COLUMN_RELEASE_DATE)),
                    type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                    coverImageUri = it.getString(it.getColumnIndexOrThrow(COLUMN_COVER_IMAGE_URI))
                )
            }
        }

        db.close()
        return movie
    }
}
