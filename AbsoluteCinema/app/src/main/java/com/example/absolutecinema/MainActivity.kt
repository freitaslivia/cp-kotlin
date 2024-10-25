package com.example.absolutecinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.absolutecinema.ui.HomeFragment
import com.example.absolutecinema.ui.AddMovieFragment
import com.example.absolutecinema.ui.EditMovieFragment
import com.example.absolutecinema.ui.MoviesToWatchFragment
import com.example.absolutecinema.ui.TeamFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_add_movie -> {
                    loadFragment(AddMovieFragment())
                    true
                }
                R.id.nav_movies_to_watch -> {
                    loadFragment(MoviesToWatchFragment())
                    true
                }
                R.id.nav_team -> {
                    loadFragment(TeamFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
