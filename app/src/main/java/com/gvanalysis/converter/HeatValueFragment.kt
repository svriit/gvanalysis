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
 * Heat Value Calculator — bidirectional.
 *
 * Formula: HV (Gcal) = Coal (tons) × GCV (kcal/kg) / 1000
 *
 * Fill any 2 of the 3 fields:
 *   Coal  = HV × 1000 / GCV
 *   GCV   = HV × 1000 / Coal
 *   HV    = Coal × GCV / 1000
 */
class HeatValueFragment : Fragment() {

    private lateinit var dataManager: DataManager

    private lateinit var sampleSpinner: AutoCompleteTextView
    private lateinit var etSampleName: TextInputEditText
    private lateinit var etCoalConsumptionHeat: TextInputEditText
    private lateinit var etGcvArb: TextInputEditText
    private lateinit var etHeatValueInput: TextInputEditText

    private lateinit var sampleSpinnerLayout: TextInputLayout
    private lateinit var sampleNameInputLayout: TextInputLayout
    private lateinit var coalConsumptionInputLayoutHeat: TextInputLayout
    private lateinit var gcvArbInputLayout: TextInputLayout
    private lateinit var heatValueInputLayout: TextInputLayout

    private lateinit var resultsContainerHeat: LinearLayout
    private lateinit var tvHeatValue: TextView
    private lateinit var tvHeatLabel: TextView
    private lateinit var tvResultHeatCoal: TextView
    private lateinit var tvResultHeatGcv: TextView

    private lateinit var btnCalculateHeat: MaterialButton
    private lateinit var btnClearHeat: MaterialButton

    private var savedSamples: List<ProximateAnalysisData> = emptyList()
    private var loadingDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_heat_value, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataManager = DataManager(requireContext())
        initViews(view)
        loadSavedSamples()
        setupSampleSpinner()
        setupButtons()
    }

    private fun initViews(view: View) {
        sampleSpinner            = view.findViewById(R.id.sampleSpinner)
        etSampleName             = view.findViewById(R.id.etSampleName)
        etCoalConsumptionHeat    = view.findViewById(R.id.etCoalConsumptionHeat)
        etGcvArb                 = view.findViewById(R.id.etGcvArb)
        etHeatValueInput         = view.findViewById(R.id.etHeatValueInput)

        sampleSpinnerLayout          = view.findViewById(R.id.sampleSpinnerLayout)
        sampleNameInputLayout        = view.findViewById(R.id.sampleNameInputLayout)
        coalConsumptionInputLayoutHeat = view.findViewById(R.id.coalConsumptionInputLayoutHeat)
        gcvArbInputLayout            = view.findViewById(R.id.gcvArbInputLayout)
        heatValueInputLayout         = view.findViewById(R.id.heatValueInputLayout)

        resultsContainerHeat = view.findViewById(R.id.resultsContainerHeat)
        tvHeatValue          = view.findViewById(R.id.tvHeatValue)
        tvHeatLabel          = view.findViewById(R.id.tvHeatLabel)
        tvResultHeatCoal     = view.findViewById(R.id.tvResultHeatCoal)
        tvResultHeatGcv      = view.findViewById(R.id.tvResultHeatGcv)

        btnCalculateHeat = view.findViewById(R.id.btnCalculateHeat)
        btnClearHeat     = view.findViewById(R.id.btnClearHeat)
    }

    private fun loadSavedSamples() { savedSamples = dataManager.getAllProximateAnalysis() }

    private fun setupSampleSpinner() {
        val names = savedSamples.map { s ->
            val label = s.sample.ifEmpty { "Sample ${s.id}" }
            "$label (${s.date}) — GCV: ${fmt(s.gcvArb)} kcal/kg"
        }
        sampleSpinner.setAdapter(ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, names))
        sampleSpinner.setOnItemClickListener { _, _, pos, _ ->
            val s = savedSamples[pos]
            etGcvArb.setText(fmt(s.gcvArb))
            if (s.sample.isNotEmpty()) etSampleName.setText(s.sample)
            Toast.makeText(requireContext(), "GCV ARB loaded: ${fmt(s.gcvArb)} kcal/kg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtons() {
        btnCalculateHeat.setOnClickListener {
            animateButtonPress(it)
            if (validateInputs()) {
                showLoadingDialog()
                Handler(Looper.getMainLooper()).postDelayed({
                    calculateResults()
                    hideLoadingDialog()
                }, 600)
            }
        }
        btnClearHeat.setOnClickListener { animateButtonPress(it); clearAllFields() }
    }

    private fun validateInputs(): Boolean {
        clearErrors()
        val coal = etCoalConsumptionHeat.text.toString().toDoubleOrNull()
        val gcv  = etGcvArb.text.toString().toDoubleOrNull()
        val hv   = etHeatValueInput.text.toString().toDoubleOrNull()

        val filled = listOfNotNull(coal, gcv, hv).size
        if (filled < 2) {
            Toast.makeText(requireContext(), "Please enter at least 2 values", Toast.LENGTH_SHORT).show()
            return false
        }
        var valid = true
        listOf(etCoalConsumptionHeat to coalConsumptionInputLayoutHeat,
               etGcvArb             to gcvArbInputLayout,
               etHeatValueInput     to heatValueInputLayout).forEach { (et, layout) ->
            val t = et.text.toString()
            if (t.isNotEmpty() && t.toDoubleOrNull() == null) {
                layout.error = getString(R.string.error_invalid_value); valid = false
            }
        }
        return valid
    }

    private fun clearErrors() {
        sampleSpinnerLayout.error = null; sampleNameInputLayout.error = null
        coalConsumptionInputLayoutHeat.error = null
        gcvArbInputLayout.error = null; heatValueInputLayout.error = null
    }

    private fun calculateResults() {
        val coal = etCoalConsumptionHeat.text.toString().toDoubleOrNull()
        val gcv  = etGcvArb.text.toString().toDoubleOrNull()
        val hv   = etHeatValueInput.text.toString().toDoubleOrNull()

        var resolvedCoal = coal
        var resolvedGcv  = gcv
        var resolvedHv   = hv
        var calculatedField = ""

        when {
            coal != null && gcv != null -> {
                resolvedHv = coal * gcv / 1000.0
                calculatedField = "Heat Value"
            }
            coal != null && hv != null -> {
                if (coal == 0.0) { gcvArbInputLayout.error = "Coal cannot be zero"; return }
                resolvedGcv = hv * 1000.0 / coal
                calculatedField = "GCV ARB"
            }
            gcv != null && hv != null -> {
                if (gcv == 0.0) { coalConsumptionInputLayoutHeat.error = "GCV cannot be zero"; return }
                resolvedCoal = hv * 1000.0 / gcv
                calculatedField = "Coal Consumption"
            }
        }

        tvHeatLabel.text = "Calculated: $calculatedField"
        tvHeatValue.text = "${fmt(resolvedHv ?: 0.0)} Gcal"
        tvResultHeatCoal.text = "${fmt(resolvedCoal ?: 0.0)} tons"
        tvResultHeatGcv.text  = "${fmt(resolvedGcv ?: 0.0)} kcal/kg"

        if (resultsContainerHeat.visibility != View.VISIBLE) {
            resultsContainerHeat.visibility = View.VISIBLE
            resultsContainerHeat.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
        }

        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        dataManager.saveHeatValue(HeatValueData(
            sampleName = etSampleName.text.toString(),
            coalConsumption = resolvedCoal ?: 0.0,
            gcvArb = resolvedGcv ?: 0.0,
            heatValue = resolvedHv ?: 0.0,
            date = dateStr
        ))
        Toast.makeText(requireContext(), "Calculation complete", Toast.LENGTH_SHORT).show()
    }

    private fun clearAllFields() {
        sampleSpinner.text = null
        etSampleName.text?.clear()
        etCoalConsumptionHeat.text?.clear()
        etGcvArb.text?.clear()
        etHeatValueInput.text?.clear()
        clearErrors()
        resultsContainerHeat.visibility = View.GONE
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
