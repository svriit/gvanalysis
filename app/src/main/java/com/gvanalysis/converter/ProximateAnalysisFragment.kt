package com.gvanalysis.converter

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
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

class ProximateAnalysisFragment : Fragment() {

    // Input fields
    private lateinit var etSample: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var etRackNumber: TextInputEditText
    private lateinit var etSource: TextInputEditText
    private lateinit var etTotalMoisture: TextInputEditText
    private lateinit var etInherentMoisture: TextInputEditText
    private lateinit var etEquilibrialMoisture: TextInputEditText
    private lateinit var etVolatileMatter: TextInputEditText
    private lateinit var etAsh: TextInputEditText

    // Input layouts for error handling
    private lateinit var sampleInputLayout: TextInputLayout
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var rackNumberInputLayout: TextInputLayout
    private lateinit var sourceInputLayout: TextInputLayout
    private lateinit var totalMoistureInputLayout: TextInputLayout
    private lateinit var inherentMoistureInputLayout: TextInputLayout
    private lateinit var equilibrialMoistureInputLayout: TextInputLayout
    private lateinit var volatileMatterInputLayout: TextInputLayout
    private lateinit var ashInputLayout: TextInputLayout

    // Result views
    private lateinit var resultsContainer: LinearLayout
    private lateinit var tvGcvAdb: TextView
    private lateinit var tvFactor: TextView
    private lateinit var tvGcvArb: TextView
    private lateinit var tvEquilibrialFactor: TextView
    private lateinit var tvCoalGrade: TextView

    // Buttons
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnClear: MaterialButton

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_proximate_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupDatePicker()
        setupButtons()
    }

    private fun initializeViews(view: View) {
        // Input fields
        etSample = view.findViewById(R.id.etSample)
        etDate = view.findViewById(R.id.etDate)
        etRackNumber = view.findViewById(R.id.etRackNumber)
        etSource = view.findViewById(R.id.etSource)
        etTotalMoisture = view.findViewById(R.id.etTotalMoisture)
        etInherentMoisture = view.findViewById(R.id.etInherentMoisture)
        etEquilibrialMoisture = view.findViewById(R.id.etEquilibrialMoisture)
        etVolatileMatter = view.findViewById(R.id.etVolatileMatter)
        etAsh = view.findViewById(R.id.etAsh)

        // Input layouts
        sampleInputLayout = view.findViewById(R.id.sampleInputLayout)
        dateInputLayout = view.findViewById(R.id.dateInputLayout)
        rackNumberInputLayout = view.findViewById(R.id.rackNumberInputLayout)
        sourceInputLayout = view.findViewById(R.id.sourceInputLayout)
        totalMoistureInputLayout = view.findViewById(R.id.totalMoistureInputLayout)
        inherentMoistureInputLayout = view.findViewById(R.id.inherentMoistureInputLayout)
        equilibrialMoistureInputLayout = view.findViewById(R.id.equilibrialMoistureInputLayout)
        volatileMatterInputLayout = view.findViewById(R.id.volatileMatterInputLayout)
        ashInputLayout = view.findViewById(R.id.ashInputLayout)

        // Result views
        resultsContainer = view.findViewById(R.id.resultsContainer)
        tvGcvAdb = view.findViewById(R.id.tvGcvAdb)
        tvFactor = view.findViewById(R.id.tvFactor)
        tvGcvArb = view.findViewById(R.id.tvGcvArb)
        tvEquilibrialFactor = view.findViewById(R.id.tvEquilibrialFactor)
        tvCoalGrade = view.findViewById(R.id.tvCoalGrade)

        // Buttons
        btnCalculate = view.findViewById(R.id.btnCalculate)
        btnClear = view.findViewById(R.id.btnClear)
    }

    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateLabel()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Set today's date by default
        updateDateLabel()
    }

    private fun updateDateLabel() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etDate.setText(dateFormat.format(calendar.time))
    }

    private fun setupButtons() {
        btnCalculate.setOnClickListener {
            // Add button press animation
            animateButtonPress(it)

            if (validateInputs()) {
                // Disable button during calculation
                btnCalculate.isEnabled = false
                btnCalculate.text = getString(R.string.calculating)

                // Simulate calculation delay for smooth UX (remove in production if needed)
                Handler(Looper.getMainLooper()).postDelayed({
                    calculateResults()
                    btnCalculate.isEnabled = true
                    btnCalculate.text = getString(R.string.calculate)
                }, 500) // 500ms delay for visual feedback
            }
        }

        btnClear.setOnClickListener {
            animateButtonPress(it)
            clearAllFields()
        }
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

        // All fields are optional - sample, date, rack, source can be empty
        // Just validate numerical fields if they have values

        // Validate numerical fields (only if they have values)
        isValid = validateNumericalField(etTotalMoisture, totalMoistureInputLayout) && isValid
        isValid = validateNumericalField(etInherentMoisture, inherentMoistureInputLayout) && isValid
        isValid = validateNumericalField(etEquilibrialMoisture, equilibrialMoistureInputLayout) && isValid
        isValid = validateNumericalField(etVolatileMatter, volatileMatterInputLayout) && isValid
        isValid = validateNumericalField(etAsh, ashInputLayout) && isValid

        return isValid
    }

    private fun validateNumericalField(
        editText: TextInputEditText,
        layout: TextInputLayout
    ): Boolean {
        val text = editText.text.toString()

        // Allow empty fields - they will be treated as 0
        if (text.isEmpty()) {
            return true
        }

        val value = text.toDoubleOrNull()
        if (value == null) {
            layout.error = getString(R.string.error_invalid_value)
            return false
        }

        if (value < 0 || value > 100) {
            layout.error = getString(R.string.error_out_of_range)
            return false
        }

        return true
    }

    private fun clearErrors() {
        sampleInputLayout.error = null
        dateInputLayout.error = null
        rackNumberInputLayout.error = null
        sourceInputLayout.error = null
        totalMoistureInputLayout.error = null
        inherentMoistureInputLayout.error = null
        equilibrialMoistureInputLayout.error = null
        volatileMatterInputLayout.error = null
        ashInputLayout.error = null
    }

    private fun calculateResults() {
        try {
            // Get values from fields (null if empty)
            val tmText = etTotalMoisture.text.toString()
            val imText = etInherentMoisture.text.toString()
            val emText = etEquilibrialMoisture.text.toString()
            val ashText = etAsh.text.toString()

            val tm = tmText.toDoubleOrNull()
            val im = imText.toDoubleOrNull()
            val em = emText.toDoubleOrNull()
            val ash = ashText.toDoubleOrNull()

            // GCV ADB = (154 * (100 - (1.1 * ash + IM)) - (108 * IM)) / 1.8
            // Requires: ash, IM
            val gcvAdb = if (ash != null && im != null) {
                (154 * (100 - (1.1 * ash + im)) - (108 * im)) / 1.8
            } else {
                0.0
            }

            // Factor = (100 - TM) / (100 - IM)
            // Requires: TM, IM
            val factor = if (tm != null && im != null && im != 100.0) {
                (100 - tm) / (100 - im)
            } else {
                0.0
            }

            // GCV ARB = Factor * GCV ADB
            // Requires: TM, IM, ash (all values needed for factor and gcvAdb)
            val gcvArb = if (tm != null && im != null && ash != null && im != 100.0) {
                factor * gcvAdb
            } else {
                0.0
            }

            // Equilibrial Factor = (100 - EM) / (100 - IM)
            // Requires: EM, IM
            val equilibrialFactor = if (em != null && im != null && im != 100.0) {
                (100 - em) / (100 - im)
            } else {
                0.0
            }

            // Display results
            displayResults(gcvAdb, factor, gcvArb, equilibrialFactor)

            Toast.makeText(requireContext(), "Calculation completed!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error in calculation: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun displayResults(
        gcvAdb: Double,
        factor: Double,
        gcvArb: Double,
        equilibrialFactor: Double
    ) {
        tvGcvAdb.text = "${roundToTwoDecimals(gcvAdb)} kcal/kg"
        tvFactor.text = roundToTwoDecimals(factor).toString()
        tvGcvArb.text = "${roundToTwoDecimals(gcvArb)} kcal/kg"
        tvEquilibrialFactor.text = roundToTwoDecimals(equilibrialFactor).toString()

        // Determine coal grade based on GCV ARB
        if (gcvArb > 0) {
            val grade = determineCoalGrade(gcvArb)
            tvCoalGrade.text = grade.gradeName
            tvCoalGrade.setTextColor(
                ContextCompat.getColor(requireContext(), grade.colorResId)
            )
        } else {
            tvCoalGrade.text = "N/A (Missing required values)"
            tvCoalGrade.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_secondary)
            )
        }

        // Show results card with animation
        if (resultsContainer.visibility != View.VISIBLE) {
            resultsContainer.visibility = View.VISIBLE
            val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            resultsContainer.startAnimation(slideUpAnimation)
        }
    }

    private fun determineCoalGrade(gcvArb: Double): CoalGrade {
        return when {
            gcvArb > 6200 -> CoalGrade.A
            gcvArb >= 5600 -> CoalGrade.B
            gcvArb >= 4940 -> CoalGrade.C
            gcvArb >= 4200 -> CoalGrade.D
            gcvArb >= 3360 -> CoalGrade.E
            gcvArb >= 2400 -> CoalGrade.F
            gcvArb >= 1300 -> CoalGrade.G
            else -> CoalGrade.G
        }
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100) / 100
    }

    private fun clearAllFields() {
        etSample.text?.clear()
        etRackNumber.text?.clear()
        etSource.text?.clear()
        etTotalMoisture.text?.clear()
        etInherentMoisture.text?.clear()
        etEquilibrialMoisture.text?.clear()
        etVolatileMatter.text?.clear()
        etAsh.text?.clear()

        clearErrors()
        resultsContainer.visibility = View.GONE

        // Reset date to today
        calendar.timeInMillis = System.currentTimeMillis()
        updateDateLabel()

        Toast.makeText(requireContext(), "Fields cleared", Toast.LENGTH_SHORT).show()
    }

    private enum class CoalGrade(val gradeName: String, val colorResId: Int) {
        A("Grade A (>6200 kcal/kg)", R.color.grade_a),
        B("Grade B (5600-6200 kcal/kg)", R.color.grade_b),
        C("Grade C (4940-5600 kcal/kg)", R.color.grade_c),
        D("Grade D (4200-4940 kcal/kg)", R.color.grade_d),
        E("Grade E (3360-4200 kcal/kg)", R.color.grade_e),
        F("Grade F (2400-3360 kcal/kg)", R.color.grade_f),
        G("Grade G (1300-2400 kcal/kg)", R.color.grade_g)
    }
}
