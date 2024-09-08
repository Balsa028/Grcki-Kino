package com.example.grckikino.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grckikino.R
import com.example.grckikino.adapters.ResultsAdapter
import com.example.grckikino.api.Result
import com.example.grckikino.models.ResultAdapterItem
import com.example.grckikino.models.ResultItem
import com.example.grckikino.utils.formatForApiCall
import com.example.grckikino.utils.yesterday
import com.example.grckikino.viewmodels.DrawingResultViewModel
import com.example.grckikino.viewmodels.DrawingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultsFragment : BaseFragment() {

    private val viewModel: DrawingResultViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[DrawingResultViewModel::class.java]
    }

    private lateinit var resultRecView: RecyclerView
    private lateinit var adapter: ResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initData()
        observeChanges()
    }

    private fun initViews(view: View) {
        resultRecView = view.findViewById(R.id.result_recycler_view)
        resultRecView.setHasFixedSize(true)
        resultRecView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ResultsAdapter()
    }

    private fun initData() {
        //results for yesterday
        viewModel.getDrawingResults(Date().yesterday().formatForApiCall(), Date().yesterday().formatForApiCall())
    }

    private fun observeChanges() {
        viewModel.resultsListResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> handleSuccessFlow(it.data?.content ?: emptyList())
                is Result.Error -> handleErrorFlow(it.message ?: resources.getString(R.string.something_went_wrong), false)
                is Result.Loading -> showProcessing()
            }
        }
    }

    private fun handleSuccessFlow(resultList: List<ResultItem>) {
        adapter.setResultItems(viewModel.prepareResultItems(resultList))
        resultRecView.adapter = adapter
        stopProcessing()
    }
}