package com.gvanalysis.converter

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class HeatValueFragment : Fragment() {

    private lateinit var dataManager: DataManager

    // Input fields
    private lateinit var sampleSpinner: AutoCompleteTextView
    private lateinit var etSampleName: TextInputEditText
    private lateinit var etCoalConsumptionHeat: TextInputEditText
    private lateinit var etGcvArb: TextInputEditText

    // Input layouts for error handling
    private lateinit var sampleSpinnerLayout: TextInputLayout
    private lateinit var sampleNameInputLayout: TextInputLayout
    private lateinit var coalConsumptionInputLayoutHeat: TextInputLayout
    private lateinit var gcvArbInputLayout: TextInputLayout

    // Result views
    private lateinit var resultsContainerHeat: LinearLayout
    private lateinit var tvHeatValue: TextView

    // Buttons
    private lateinit var btnCalculateHeat: MaterialButton
    private lateinit var btnClearHeat: MaterialButton

    // Saved samples
    private var savedSamples: List<ProximateAnalysisData> = emptyList()
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_heat_value, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        initializeViews(view)
        loadSavedSamples()
        setupButtons()
        setupSampleSpinner()
    }

    private fun initializeViews(view: View) {
        // Input fields
        sampleSpinner = view.findViewById(R.id.sampleSpinner)
        etSampleName = view.findViewById(R.id.etSampleName)
        etCoalConsumptionHeat = view.findViewById(R.id.etCoalConsumptionHeat)
        etGcvArb = view.findViewById(R.id.etGcvArb)

        // Input layouts
        sampleSpinnerLayout = view.findViewById(R.id.sampleSpinnerLayout)
        sampleNameInputLayout = view.findViewById(R.id.sampleNameInputLayout)
        coalConsumptionInputLayoutHeat = view.findViewById(R.id.coalConsumptionInputLayoutHeat)
        gcvArbInputLayout = view.findViewById(R.id.gcvArbInputLayout)

        // Result views
        resultsContainerHeat = view.findViewById(R.id.resultsContainerHeat)
        tvHeatValue = view.findViewById(R.id.tvHeatValue)

        // Buttons
        btnCalculateHeat = view.findViewById(R.id.btnCalculateHeat)
        btnClearHeat = view.findViewById(R.id.btnClearHeat)
    }

    private fun loadSavedSamples() {
        savedSamples = dataManager.getAllProximateAnalysis()
    }

    private fun setupSampleSpinner() {
        val sampleNames = savedSamples.map { sample ->
            if (sample.sample.isNotEmpty()) {
                "${sample.sample} (${sample.date}) - GCV: ${roundToTwoDecimals(sample.gcvArb)} kcal/kg"
            } else {
                "Sample ${sample.id} (${sample.date}) - GCV: ${roundToTwoDecimals(sample.gcvArb)} kcal/kg"
            }
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            sampleNames
        )
        sampleSpinner.setAdapter(adapter)

        sampleSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedSample = savedSamples[position]
            // Auto-fill GCV ARB from selected sample
            etGcvArb.setText(roundToTwoDecimals(selectedSample.gcvArb).toString())
            // Set sample name
            if (selectedSample.sample.isNotEmpty()) {
                etSampleName.setText(selectedSample.sample)
            }
            Toast.makeText(
                requireContext(),
                "Sample loaded: GCV ARB = ${roundToTwoDecimals(selectedSample.gcvArb)} kcal/kg",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Show message if no samples available
        if (savedSamples.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "No saved samples found. Enter values manually.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupButtons() {
        btnCalculateHeat.setOnClickListener {
            // Add button press animation
            animateButtonPress(it)

            if (validateInputs()) {
                // Show loading dialog
                showLoadingDialog()

                // Simulate calculation delay for smooth UX
                Handler(Looper.getMainLooper()).postDelayed({
                    calculateResults()
                    hideLoadingDialog()
                }, 800) // 800ms delay for visual feedback
            }
        }

        btnClearHeat.setOnClickListener {
            animateButtonPress(it)
            clearAllFields()
        }
    }

    private fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext())
        loadingDialog?.apply {
            setContentView(android.R.layout.simple_list_item_1)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)

            // Create custom loading layout
            val progressBar = android.widget.ProgressBar(requireContext())
            progressBar.indeterminateTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.primary)
            )

            val container = android.widget.LinearLayout(requireContext())
            container.orientation = android.widget.LinearLayout.VERTICAL
            container.gravity = android.view.Gravity.CENTER
            container.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.loading_background))
            container.setPadding(80, 80, 80, 80)

            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 32)
            container.addView(progressBar, params)

            val textView = TextView(requireContext())
            textView.text = "Calculating..."
            textView.textSize = 16f
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            textView.gravity = android.view.Gravity.CENTER
            container.addView(textView)

            setContentView(container)

            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.7).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawableResource(R.drawable.card_elevated)

            show()
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun animateButtonPress(view: View) {
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f)
        scaleDown.duration = 100
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f)
        scaleDownY.duration = 100

        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f)
        scaleUp.duration = 100
        scaleUp.startDelay = 100
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)
        scaleUpY.duration = 100
        scaleUpY.startDelay = 100

        scaleDown.start()
        scaleDownY.start()
        scaleUp.start()
        scaleUpY.start()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear all errors first
        clearErrors()

        // Validate coal consumption
        val coalConsumptionText = etCoalConsumptionHeat.text.toString()
        if (coalConsumptionText.isEmpty()) {
            coalConsumptionInputLayoutHeat.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            val value = coalConsumptionText.toDoubleOrNull()
            if (value == null || value < 0) {
                coalConsumptionInputLayoutHeat.error = getString(R.string.error_invalid_value)
                isValid = false
            }
        }

        // Validate GCV ARB
        val gcvArbText = etGcvArb.text.toString()
        if (gcvArbText.isEmpty()) {
            gcvArbInputLayout.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            val value = gcvArbText.toDoubleOrNull()
            if (value == null || value <= 0) {
                gcvArbInputLayout.error = getString(R.string.error_invalid_value)
                isValid = false
            }
        }

        return isValid
    }

    private fun clearErrors() {
        sampleSpinnerLayout.error = null
        sampleNameInputLayout.error = null
        coalConsumptionInputLayoutHeat.error = null
        gcvArbInputLayout.error = null
    }

    private fun calculateResults() {
        try {
            val coalConsumption = etCoalConsumptionHeat.text.toString().toDoubleOrNull() ?: 0.0
            val gcvArb = etGcvArb.text.toString().toDoubleOrNull() ?: 0.0

            // Formula: heat value = coal consumption (tons) * GCV ARB (kcal/kg)
            // Convert tons to kg: multiply by 1000
            // Result in kcal, then convert to Gcal by dividing by 1,000,000
            val heatValueKcal = coalConsumption * 1000 * gcvArb
            val heatValueGcal = heatValueKcal / 1_000_000

            // Display results
            displayResults(heatValueGcal)

            // Save data
            saveHeatValueData(coalConsumption, gcvArb, heatValueGcal)

            Toast.makeText(requireContext(), "Results calculated and saved!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error in calculation: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun displayResults(heatValueGcal: Double) {
        tvHeatValue.text = "${roundToTwoDecimals(heatValueGcal)} Gcal"

        // Show results card with animation
        if (resultsContainerHeat.visibility != View.VISIBLE) {
            resultsContainerHeat.visibility = View.VISIBLE
            val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            resultsContainerHeat.startAnimation(slideUpAnimation)
        }
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100) / 100
    }

    private fun clearAllFields() {
        sampleSpinner.text = null
        etSampleName.text?.clear()
        etCoalConsumptionHeat.text?.clear()
        etGcvArb.text?.clear()

        clearErrors()
        resultsContainerHeat.visibility = View.GONE

        Toast.makeText(requireContext(), "Fields cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveHeatValueData(
        coalConsumption: Double,
        gcvArb: Double,
        heatValue: Double
    ) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val data = HeatValueData(
            sampleName = etSampleName.text.toString(),
            coalConsumption = coalConsumption,
            gcvArb = gcvArb,
            heatValue = heatValue,
            date = currentDate
        )
        dataManager.saveHeatValue(data)
    }
}
