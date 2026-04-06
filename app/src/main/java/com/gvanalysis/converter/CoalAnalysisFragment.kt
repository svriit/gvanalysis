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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

/**
 * SCC Calculator — bidirectional.
 *
 * Formula: SCC (kg/kWh) = Coal (tons) / (Unit (kWh) × 1000)
 *
 * Fill any 2 of the 3 fields; the third is calculated:
 *   Coal  = SCC × Unit × 1000
 *   Unit  = Coal / (SCC × 1000)
 *   SCC   = Coal / (Unit × 1000)
 */
class CoalAnalysisFragment : Fragment() {

    private lateinit var dataManager: DataManager

    private lateinit var etCoalConsumption: TextInputEditText
    private lateinit var etUnitGeneration: TextInputEditText
    private lateinit var etScc: TextInputEditText

    private lateinit var coalConsumptionInputLayout: TextInputLayout
    private lateinit var unitGenerationInputLayout: TextInputLayout
    private lateinit var sccInputLayout: TextInputLayout

    private lateinit var resultsContainerCoal: LinearLayout
    private lateinit var tvSpecificCoalConsumption: TextView
    private lateinit var tvSccLabel: TextView
    private lateinit var tvResultCoal: TextView
    private lateinit var tvResultUnit: TextView

    private lateinit var btnCalculateCoal: MaterialButton
    private lateinit var btnClearCoal: MaterialButton

    private var loadingDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_coal_analysis, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataManager = DataManager(requireContext())
        initViews(view)
        setupButtons()
    }

    private fun initViews(view: View) {
        etCoalConsumption = view.findViewById(R.id.etCoalConsumption)
        etUnitGeneration  = view.findViewById(R.id.etUnitGeneration)
        etScc             = view.findViewById(R.id.etScc)

        coalConsumptionInputLayout = view.findViewById(R.id.coalConsumptionInputLayout)
        unitGenerationInputLayout  = view.findViewById(R.id.unitGenerationInputLayout)
        sccInputLayout             = view.findViewById(R.id.sccInputLayout)

        resultsContainerCoal       = view.findViewById(R.id.resultsContainerCoal)
        tvSpecificCoalConsumption  = view.findViewById(R.id.tvSpecificCoalConsumption)
        tvSccLabel                 = view.findViewById(R.id.tvSccLabel)
        tvResultCoal               = view.findViewById(R.id.tvResultCoal)
        tvResultUnit               = view.findViewById(R.id.tvResultUnit)

        btnCalculateCoal = view.findViewById(R.id.btnCalculateCoal)
        btnClearCoal     = view.findViewById(R.id.btnClearCoal)
    }

    private fun setupButtons() {
        btnCalculateCoal.setOnClickListener {
            animateButtonPress(it)
            if (validateInputs()) {
                showLoadingDialog()
                Handler(Looper.getMainLooper()).postDelayed({
                    calculateResults()
                    hideLoadingDialog()
                }, 600)
            }
        }
        btnClearCoal.setOnClickListener {
            animateButtonPress(it)
            clearAllFields()
        }
    }

    private fun validateInputs(): Boolean {
        clearErrors()
        var coal = etCoalConsumption.text.toString().toDoubleOrNull()
        var unit = etUnitGeneration.text.toString().toDoubleOrNull()
        var scc  = etScc.text.toString().toDoubleOrNull()

        val filled = listOfNotNull(coal, unit, scc).size
        if (filled < 2) {
            Toast.makeText(requireContext(), "Please enter at least 2 values", Toast.LENGTH_SHORT).show()
            return false
        }

        var valid = true
        listOf(
            etCoalConsumption to coalConsumptionInputLayout,
            etUnitGeneration  to unitGenerationInputLayout,
            etScc             to sccInputLayout
        ).forEach { (et, layout) ->
            val t = et.text.toString()
            if (t.isNotEmpty() && t.toDoubleOrNull() == null) {
                layout.error = getString(R.string.error_invalid_value); valid = false
            }
        }
        return valid
    }

    private fun clearErrors() {
        coalConsumptionInputLayout.error = null
        unitGenerationInputLayout.error  = null
        sccInputLayout.error             = null
    }

    private fun calculateResults() {
        val coal = etCoalConsumption.text.toString().toDoubleOrNull()
        val unit = etUnitGeneration.text.toString().toDoubleOrNull()
        val scc  = etScc.text.toString().toDoubleOrNull()

        var resolvedCoal = coal
        var resolvedUnit = unit
        var resolvedScc  = scc
        var calculatedField = ""

        when {
            coal != null && unit != null -> {
                // Forward: calculate SCC
                if (unit == 0.0) { sccInputLayout.error = "Unit Generation cannot be zero"; return }
                resolvedScc  = coal / (unit * 1000)
                calculatedField = "SCC"
            }
            coal != null && scc != null -> {
                // Solve for Unit
                if (scc == 0.0) { unitGenerationInputLayout.error = "SCC cannot be zero"; return }
                resolvedUnit = coal / (scc * 1000)
                calculatedField = "Unit Generation"
            }
            unit != null && scc != null -> {
                // Solve for Coal
                resolvedCoal = scc * unit * 1000
                calculatedField = "Coal Consumption"
            }
        }

        // Show results
        tvSccLabel.text = "Calculated: $calculatedField"
        tvSpecificCoalConsumption.text = "${fmt(resolvedScc ?: 0.0)} kg/kWh"
        tvResultCoal.text = "${fmt(resolvedCoal ?: 0.0)} tons"
        tvResultUnit.text = "${fmt(resolvedUnit ?: 0.0)} kWh"

        if (resultsContainerCoal.visibility != View.VISIBLE) {
            resultsContainerCoal.visibility = View.VISIBLE
            resultsContainerCoal.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
        }

        // Save
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        dataManager.saveCoalAnalysis(
            CoalAnalysisData(
                coalConsumption = resolvedCoal ?: 0.0,
                unitGeneration  = resolvedUnit ?: 0.0,
                specificCoalConsumption = resolvedScc ?: 0.0,
                date = dateStr
            )
        )
        Toast.makeText(requireContext(), "Calculation complete", Toast.LENGTH_SHORT).show()
    }

    private fun clearAllFields() {
        etCoalConsumption.text?.clear()
        etUnitGeneration.text?.clear()
        etScc.text?.clear()
        clearErrors()
        resultsContainerCoal.visibility = View.GONE
        Toast.makeText(requireContext(), "Fields cleared", Toast.LENGTH_SHORT).show()
    }

    private fun fmt(v: Double): String = (round(v * 10000.0) / 10000.0).toString()

    private fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext()).apply {
            val container = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.loading_background))
                setPadding(80, 80, 80, 80)
                val pb = android.widget.ProgressBar(requireContext()).apply {
                    indeterminateTintList = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.primary))
                }
                addView(pb, android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 32) })
                val tv = TextView(requireContext()).apply {
                    text = "Calculating..."; textSize = 16f; gravity = android.view.Gravity.CENTER
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                }
                addView(tv)
            }
            setContentView(container)
            window?.setLayout((resources.displayMetrics.widthPixels * 0.7).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawableResource(R.drawable.card_elevated)
            setCancelable(false)
            show()
        }
    }

    private fun hideLoadingDialog() { loadingDialog?.dismiss(); loadingDialog = null }

    private fun animateButtonPress(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f).apply { duration = 100; start() }
        ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f).apply { duration = 100; start() }
        ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).apply { duration = 100; startDelay = 100; start() }
        ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).apply { duration = 100; startDelay = 100; start() }
    }
}
