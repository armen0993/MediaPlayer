package com.example.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        //showBottomBar()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_fragment -> navController.navigate(R.id.homeFragment)
                R.id.songs_fragment-> navController.navigate(R.id.songsFragment)
            }
            return@setOnItemSelectedListener true
        }



//        binding.bottomBar.setOnItemSelectedListener() {
//            NavigationUI.onNavDestinationSelected(
//                it,
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//            )
//            return@setOnItemSelectedListener true
//        }
//        binding.bottomBar.setOnItemReselectedListener() {
//        }
    }

//    private fun showBottomBar() {
//        binding.bottomBar.setupWithNavController(
//            Navigation.findNavController(
//                this,
//                R.id.nav_host_fragment
//            )
//        )
//        binding.bottomBar.setOnItemSelectedListener() {
//            NavigationUI.onNavDestinationSelected(
//                it,
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//            )
//            return@setOnItemSelectedListener true
//        }
//        binding.bottomBar.setOnItemReselectedListener() {
//        }
//    }
}