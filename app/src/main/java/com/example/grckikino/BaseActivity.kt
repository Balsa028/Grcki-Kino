package com.example.grckikino

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.grckikino.interfaces.BaseCoordinator
import com.example.grckikino.utils.toPx
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity : AppCompatActivity(), BaseCoordinator {

    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarIcon: ImageView
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarWrapper: LinearLayout
    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        initViews()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        setupNavigationView()
    }

    private fun initViews() {
        toolbarIcon = toolbar.findViewById(R.id.toolbar_icon)
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title)
        toolbarWrapper = toolbar.findViewById(R.id.toolbar_wrapper)
        navigationView = toolbar.findViewById(R.id.navigation_view)
    }

    override fun adjustToolbarForDrawingDetailsScreen() {
        val layoutParams = toolbarIcon.layoutParams
        layoutParams.width = 27.toPx().toInt()
        layoutParams.height = 27.toPx().toInt()
        toolbarIcon.layoutParams = layoutParams

        toolbarIcon.setOnClickListener { onBackPressed() }
        Glide.with(this)
            .load(R.drawable.arrow_left)
            .into(toolbarIcon)

        toolbarTitle.visibility = View.VISIBLE
        toolbarWrapper.setPadding(10.toPx().toInt(), 10.toPx().toInt(), 10.toPx().toInt(), 5.toPx().toInt())
        navigationView.visibility = View.VISIBLE
    }

    override fun adjustToolbarForDrawingsListScreen() {
        toolbarWrapper.setPadding(0)
        toolbarTitle.visibility = View.GONE
        navigationView.visibility = View.GONE
        navigationView.selectedItemId = R.id.talon

        val layoutParams = toolbarIcon.layoutParams
        layoutParams.width = 50.toPx().toInt()
        layoutParams.height = 50.toPx().toInt()
        toolbarIcon.layoutParams = layoutParams

        Glide.with(this)
            .load(R.drawable.mozzart)
            .into(toolbarIcon)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.resultsFragment ||
            navController.currentDestination?.id == R.id.drawingAnimationFragment ||
            navController.currentDestination?.id == R.id.drawingDetailsFragment) {
            navController.popBackStack(R.id.drawingListFragment, false)
        } else super.onBackPressed()
    }

    private fun setupNavigationView() {
        navigationView.setupWithNavController(navController)
        navigationView.setOnItemSelectedListener { menuItem ->
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