package com.example.grckikino

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.grckikino.interfaces.BaseCoordinator
import com.example.grckikino.utils.toPx
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity : AppCompatActivity(), BaseCoordinator {

    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        setupNavigationView()
    }

    override fun adjustToolbarForDrawingDetailsScreen() {
        val backArrow = toolbar.findViewById<ImageView>(R.id.toolbar_icon)
        val layoutParams = backArrow.layoutParams
        layoutParams.width = 27.toPx().toInt()
        layoutParams.height = 27.toPx().toInt()
        backArrow.layoutParams = layoutParams

        backArrow.setOnClickListener { navController.navigateUp() }
        Glide.with(this)
            .load(R.drawable.arrow_left)
            .into(backArrow)

        toolbar.findViewById<TextView>(R.id.toolbar_title).visibility = View.VISIBLE
        toolbar.findViewById<LinearLayout>(R.id.toolbar_wrapper)
            .setPadding(10.toPx().toInt(), 10.toPx().toInt(), 10.toPx().toInt(), 5.toPx().toInt())
        toolbar.findViewById<BottomNavigationView>(R.id.navigation_view).visibility = View.VISIBLE
    }

    override fun adjustToolbarForDrawingsListScreen() {
        toolbar.findViewById<LinearLayout>(R.id.toolbar_wrapper).setPadding(0)
        toolbar.findViewById<TextView>(R.id.toolbar_title).visibility = View.GONE
        toolbar.findViewById<BottomNavigationView>(R.id.navigation_view).visibility = View.GONE

        val toolbarIcon = toolbar.findViewById<ImageView>(R.id.toolbar_icon)
        val layoutParams = toolbarIcon.layoutParams
        layoutParams.width = 50.toPx().toInt()
        layoutParams.height = 50.toPx().toInt()
        toolbarIcon.layoutParams = layoutParams

        Glide.with(this)
            .load(R.drawable.mozzart)
            .into(toolbarIcon)
    }

    private fun setupNavigationView() {
        toolbar.findViewById<BottomNavigationView>(R.id.navigation_view)
            .setupWithNavController(navController)
        toolbar.findViewById<BottomNavigationView>(R.id.navigation_view)
            .setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.talon -> {
                        navController.popBackStack(R.id.drawingDetailsFragment, false)
                        true
                    }

                    R.id.play_drawing -> {
                        navController.navigate(R.id.drawingAnimationFragment)
                        true
                    }

                    R.id.results -> {
                        navController.navigate(R.id.resultsFragment)
                        true
                    }

                    else -> false
                }
            }
    }
}