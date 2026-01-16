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

class CoalAnalysisFragment : Fragment() {

    private lateinit var dataManager: DataManager

    // Input fields
    private lateinit var etCoalConsumption: TextInputEditText
    private lateinit var etUnitGeneration: TextInputEditText

    // Input layouts for error handling
    private lateinit var coalConsumptionInputLayout: TextInputLayout
    private lateinit var unitGenerationInputLayout: TextInputLayout

    // Result views
    private lateinit var resultsContainerCoal: LinearLayout
    private lateinit var tvSpecificCoalConsumption: TextView

    // Buttons
    private lateinit var btnCalculateCoal: MaterialButton
    private lateinit var btnClearCoal: MaterialButton

    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_coal_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        initializeViews(view)
        setupButtons()
    }

    private fun initializeViews(view: View) {
        // Input fields
        etCoalConsumption = view.findViewById(R.id.etCoalConsumption)
        etUnitGeneration = view.findViewById(R.id.etUnitGeneration)

        // Input layouts
        coalConsumptionInputLayout = view.findViewById(R.id.coalConsumptionInputLayout)
        unitGenerationInputLayout = view.findViewById(R.id.unitGenerationInputLayout)

        // Result views
        resultsContainerCoal = view.findViewById(R.id.resultsContainerCoal)
        tvSpecificCoalConsumption = view.findViewById(R.id.tvSpecificCoalConsumption)

        // Buttons
        btnCalculateCoal = view.findViewById(R.id.btnCalculateCoal)
        btnClearCoal = view.findViewById(R.id.btnClearCoal)
    }

    private fun setupButtons() {
        btnCalculateCoal.setOnClickListener {
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

        btnClearCoal.setOnClickListener {
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
        val coalConsumptionText = etCoalConsumption.text.toString()
        if (coalConsumptionText.isEmpty()) {
            coalConsumptionInputLayout.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            val value = coalConsumptionText.toDoubleOrNull()
            if (value == null || value < 0) {
                coalConsumptionInputLayout.error = getString(R.string.error_invalid_value)
                isValid = false
            }
        }

        // Validate unit generation
        val unitGenerationText = etUnitGeneration.text.toString()
        if (unitGenerationText.isEmpty()) {
            unitGenerationInputLayout.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            val value = unitGenerationText.toDoubleOrNull()
            if (value == null || value <= 0) {
                unitGenerationInputLayout.error = getString(R.string.error_invalid_value)
                isValid = false
            }
        }

        return isValid
    }

    private fun clearErrors() {
        coalConsumptionInputLayout.error = null
        unitGenerationInputLayout.error = null
    }

    private fun calculateResults() {
        try {
            val coalConsumption = etCoalConsumption.text.toString().toDoubleOrNull() ?: 0.0
            val unitGeneration = etUnitGeneration.text.toString().toDoubleOrNull() ?: 0.0

            // Formula: specific coal consumption = (coal consumption / unit generation) / 1000
            val specificCoalConsumption = if (unitGeneration > 0) {
                (coalConsumption / unitGeneration) / 1000
            } else {
                0.0
            }

            // Display results
            displayResults(specificCoalConsumption)

            // Save data
            saveCoalAnalysisData(coalConsumption, unitGeneration, specificCoalConsumption)

            Toast.makeText(requireContext(), "Results calculated and saved!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error in calculation: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun displayResults(specificCoalConsumption: Double) {
        tvSpecificCoalConsumption.text = "${roundToTwoDecimals(specificCoalConsumption)} kg/kWh"

        // Show results card with animation
        if (resultsContainerCoal.visibility != View.VISIBLE) {
            resultsContainerCoal.visibility = View.VISIBLE
            val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            resultsContainerCoal.startAnimation(slideUpAnimation)
        }
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100) / 100
    }

    private fun clearAllFields() {
        etCoalConsumption.text?.clear()
        etUnitGeneration.text?.clear()

        clearErrors()
        resultsContainerCoal.visibility = View.GONE

        Toast.makeText(requireContext(), "Fields cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveCoalAnalysisData(
        coalConsumption: Double,
        unitGeneration: Double,
        specificCoalConsumption: Double
    ) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val data = CoalAnalysisData(
            coalConsumption = coalConsumption,
            unitGeneration = unitGeneration,
            specificCoalConsumption = specificCoalConsumption,
            date = currentDate
        )
        dataManager.saveCoalAnalysis(data)
    }
}
