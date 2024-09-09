package com.example.grckikino.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grckikino.R
import com.example.grckikino.adapters.NumbersAdapter
import com.example.grckikino.adapters.ResultNumbersAdapter
import com.example.grckikino.api.Result
import com.example.grckikino.models.Drawing
import com.example.grckikino.models.GridNumber
import com.example.grckikino.utils.REMAINING_TIME_DIALOG_WARNING
import com.example.grckikino.utils.REMAINING_TIME_WARNING
import com.example.grckikino.utils.formatDrawingTimeForDisplay
import com.example.grckikino.viewmodels.DrawingDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingDetailsFragment : BaseFragment() {

    var selectedSpinnerNumber: Int = 0
    var gridNumbers: List<GridNumber> = emptyList()
    var drawId: Int = 0
    var isApiAlreadyCalled = false
    private val args: DrawingDetailsFragmentArgs by navArgs()

    private val viewModel: DrawingDetailsViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[DrawingDetailsViewModel::class.java]
    }

    private lateinit var spinner: Spinner
    private lateinit var numberRecView: RecyclerView
    private lateinit var adapter: NumbersAdapter
    private lateinit var txtRemainingTime: TextView
    private lateinit var txtDrawingTime: TextView
    private lateinit var txtDrawId: TextView
    private lateinit var btnRandom: Button
    private lateinit var selectedNumbersRecView: RecyclerView
    private lateinit var deleteAllImageView: ImageView
    private lateinit var selectedNumbersContainer: LinearLayout
    private lateinit var selectedNumbersAdapter: ResultNumbersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drawing_details, container, false)
        initViews(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeChanges()
    }

    private fun observeChanges() {
        viewModel.drawingResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    handleSuccessFlow(result.data)
                    isApiAlreadyCalled = true
                }
                is Result.Error -> handleErrorFlow(result.message ?: ContextCompat.getString(requireActivity(), R.string.something_went_wrong), true)
                is Result.Loading -> showProcessing()
            }
        }
        viewModel.updateRemainingTime.observe(viewLifecycleOwner) { handleRemainingTimeChanges(it) }
        viewModel.updateRandomSelectedItems.observe(viewLifecycleOwner) { randomlySelectedNumbers ->
            randomlySelectedNumbers.forEach { adapter.notifyItemChanged(it - 1) }
            adapter.increaseNumbersCounter(randomlySelectedNumbers.size)
            viewModel.updateSavedSelections(randomlySelectedNumbers)
        }
        viewModel.restoreSavedSelections.observe(viewLifecycleOwner) { savedNumbers ->
            savedNumbers.forEach { adapter.notifyItemChanged(it - 1) }
        }
        viewModel.removeItemOnIndex.observe(viewLifecycleOwner) { index -> adapter.notifyItemChanged(index) }
        viewModel.increaseSelectedCounter.observe(viewLifecycleOwner) { value -> adapter.increaseNumbersCounter(value) }
        viewModel.decreaseSelectedCounter.observe(viewLifecycleOwner) { value -> adapter.decreaseNumbersCounter(value) }
        viewModel.savedSelectedItems.observe(viewLifecycleOwner) { selectedList ->
            if (selectedList.isNotEmpty()) {
                selectedNumbersContainer.visibility = View.VISIBLE
                selectedNumbersAdapter.setNumbers(selectedList)
            } else {
                selectedNumbersContainer.visibility = View.GONE
            }
        }
    }


    private fun initViews(view: View) {
        txtRemainingTime = view.findViewById(R.id.remaining_time_textview)
        txtDrawingTime = view.findViewById(R.id.drawing_time_result_textview)
        txtDrawId = view.findViewById(R.id.drawing_id_results_textview)
        deleteAllImageView = view.findViewById(R.id.deleteAllImageView)
        deleteAllImageView.setOnClickListener { viewModel.clearSelectedNumber(gridNumbers) }

        btnRandom = view.findViewById(R.id.btnRandom)
        btnRandom.setOnClickListener {
            viewModel.selectRandomNumbers(selectedSpinnerNumber, gridNumbers)
        }

        spinner = view.findViewById(R.id.spinner_numbers)
        setupSpinner()

        adapter = NumbersAdapter { clickedNumber ->
            viewModel.handleSelectingBehavior(clickedNumber)
        }
        numberRecView = view.findViewById(R.id.talonRecView)
        numberRecView.layoutManager = GridLayoutManager(requireActivity(), 10)

        selectedNumbersContainer = view.findViewById(R.id.selectedNumbersContainer)
        selectedNumbersRecView = view.findViewById(R.id.selectedNumbersRecView)
        selectedNumbersRecView.layoutManager = GridLayoutManager(requireActivity(), 4)
        selectedNumbersAdapter = ResultNumbersAdapter()
        selectedNumbersRecView.adapter = selectedNumbersAdapter
    }

    private fun setupSpinner() {
        val spinnerNumbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        val arrayAdapter = ArrayAdapter(
            requireActivity(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            spinnerNumbers
        )
        arrayAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedSpinnerNumber = parent?.getItemAtPosition(position) as Int
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun initData() {
        coordinator?.adjustToolbarForDrawingDetailsScreen()
        drawId = args.drawId
        viewModel.getDrawingDetails(drawId)
    }

    private fun setupAdapter() {
        adapter.setNumbersList(gridNumbers)
        numberRecView.adapter = adapter

        numberRecView.post {
            stopProcessing()
        }
    }

    private fun handleSuccessFlow(drawing: Drawing?) {
        drawing?.let {
            if (!isApiAlreadyCalled) {
                populateAdapterWithData()
                setDrawingDetailsData(it)
                viewModel.startUpdatingRemainingTime(it.drawTime)
                viewModel.saveCurrentDrawing(it)
            } else {
                showProcessing()
                setupAdapter()
                setDrawingDetailsData(it)
            }
        }
    }

    private fun setDrawingDetailsData(drawing: Drawing) {
        txtDrawingTime.text = drawing.drawTime.formatDrawingTimeForDisplay()
        txtDrawId.text = drawing.drawId.toString()
    }

    private fun populateAdapterWithData() {
        lifecycleScope.launch(Dispatchers.Default) {
            delay(300) // waiting for animation to end properly in order to work good
            gridNumbers = (1..80).map { GridNumber(it) }
            withContext(Dispatchers.Main) {
                setupAdapter()
            }
        }
    }

    private fun handleRemainingTimeChanges(formatedText: String) {
        txtRemainingTime.text = formatedText
        txtRemainingTime.setTextColor(
            ContextCompat.getColor(
                requireActivity(),
                if (formatedText <= REMAINING_TIME_WARNING || formatedText == resources.getString(R.string.past)) R.color.red else R.color.white
            )
        )
        if (formatedText == REMAINING_TIME_DIALOG_WARNING)
            showAlertDialog(resources.getString(R.string.warning_title), resources.getString(R.string.warning_description), resources.getString(R.string.OK), false)
        if (formatedText == resources.getString(R.string.past))
            viewModel.clearSelectedNumber(gridNumbers)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        viewModel.handleSavedSelections()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearSelectedNumber(gridNumbers)
    }
}