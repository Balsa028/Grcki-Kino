package com.example.grckikino.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grckikino.R
import com.example.grckikino.adapters.DrawingsAdapterListener
import com.example.grckikino.adapters.DrawingsAdapter
import com.example.grckikino.api.Result
import com.example.grckikino.models.Drawing
import com.example.grckikino.viewmodels.DrawingsViewModel

class DrawingsFragment : BaseFragment(), DrawingsAdapterListener {

    private val viewModel: DrawingsViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[DrawingsViewModel::class.java]
    }

    private lateinit var drawingRecyclerView: RecyclerView
    private lateinit var drawingsAdapter: DrawingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drawings, container, false)
        initViews(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getDrawingsList()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeChanges()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(drawingsAdapter.getDrawingList())
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private  fun initViews(view: View) {
        drawingRecyclerView = view.findViewById(R.id.drawing_list_rec_view)
        setupRecycleView()
    }

    private fun setupRecycleView() {
        drawingsAdapter = DrawingsAdapter()
        drawingsAdapter.setAdapterListener(this)
        drawingRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        drawingRecyclerView.setHasFixedSize(true)
        drawingRecyclerView.adapter = drawingsAdapter
    }

    private fun observeChanges() {
        viewModel.drawingListResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> handleSuccessFlow(result.data)
                is Result.Error -> handleErrorFlow(result.message ?: ContextCompat.getString(requireActivity(), R.string.something_went_wrong))
                is Result.Loading -> showProcessing()
            }
        }

        viewModel.updatedDrawingList.observe(viewLifecycleOwner) {
            drawingsAdapter.setDrawingList(it)
        }
    }

    private fun handleSuccessFlow(drawingList: List<Drawing>?) {
        stopProcessing()
        drawingList?.let {
            drawingsAdapter.setDrawingList(it)
            viewModel.startUpdatingRemainingTime(it)
        }
    }

    private fun handleErrorFlow(message: String) {
        stopProcessing()
        showAlertDialog(ContextCompat.getString(requireActivity(), R.string.error_occurred), message, ContextCompat.getString(requireActivity(), R.string.OK))
    }

    override fun onToDrawingDetailsScreen(gameId: Int, drawId: Int) {
        findNavController().navigate(R.id.action_drawingListFragment_to_drawingDetailsFragment)
    }
}