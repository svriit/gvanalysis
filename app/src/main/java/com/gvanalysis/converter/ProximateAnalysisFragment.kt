package com.gvanalysis.converter

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
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

class ProximateAnalysisFragment : Fragment() {

    private lateinit var dataManager: DataManager

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
    private lateinit var tvCoalGradeRange: TextView

    // Buttons
    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnClear: MaterialButton

    private val calendar = Calendar.getInstance()
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_proximate_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
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
        tvCoalGradeRange = view.findViewById(R.id.tvCoalGradeRange)

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
                // Show loading dialog
                showLoadingDialog()

                // Simulate calculation delay for smooth UX
                Handler(Looper.getMainLooper()).postDelayed({
                    calculateResults()
                    hideLoadingDialog()
                }, 800) // 800ms delay for visual feedback
            }
        }

        btnClear.setOnClickListener {
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
            val grade = if (gcvArb > 0) determineCoalGrade(gcvArb).gradeName else "N/A"
            displayResults(gcvAdb, factor, gcvArb, equilibrialFactor)

            // Save data
            saveProximateAnalysisData(tm ?: 0.0, im ?: 0.0, em ?: 0.0, ash ?: 0.0, gcvAdb, factor, gcvArb, equilibrialFactor, grade)

            Toast.makeText(requireContext(), "Results calculated and saved!", Toast.LENGTH_SHORT).show()

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
            // Split "G1  >7000 kcal/kg" into label + range for the two TextViews
            val parts = grade.gradeName.split("  ", limit = 2)
            tvCoalGrade.text = parts[0]          // e.g. "G1"
            tvCoalGradeRange.text = if (parts.size > 1) parts[1] else ""
            val gradeColor = ContextCompat.getColor(requireContext(), grade.colorResId)
            tvCoalGrade.setTextColor(gradeColor)
            tvCoalGradeRange.setTextColor(gradeColor)
        } else {
            tvCoalGrade.text = "N/A"
            tvCoalGradeRange.text = "Missing required values"
            tvCoalGrade.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            tvCoalGradeRange.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
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
            gcvArb > 7000 -> CoalGrade.G1
            gcvArb > 6700 -> CoalGrade.G2
            gcvArb > 6400 -> CoalGrade.G3
            gcvArb > 6100 -> CoalGrade.G4
            gcvArb > 5800 -> CoalGrade.G5
            gcvArb > 5500 -> CoalGrade.G6
            gcvArb > 5200 -> CoalGrade.G7
            gcvArb > 4900 -> CoalGrade.G8
            gcvArb > 4600 -> CoalGrade.G9
            gcvArb > 4300 -> CoalGrade.G10
            gcvArb > 4000 -> CoalGrade.G11
            gcvArb > 3700 -> CoalGrade.G12
            gcvArb > 3400 -> CoalGrade.G13
            gcvArb > 3100 -> CoalGrade.G14
            gcvArb > 2800 -> CoalGrade.G15
            gcvArb > 2500 -> CoalGrade.G16
            else           -> CoalGrade.G17
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

    private fun saveProximateAnalysisData(
        tm: Double, im: Double, em: Double, ash: Double,
        gcvAdb: Double, factor: Double, gcvArb: Double, equilibrialFactor: Double, grade: String
    ) {
        val data = ProximateAnalysisData(
            sample = etSample.text.toString(),
            date = etDate.text.toString(),
            rackNumber = etRackNumber.text.toString(),
            source = etSource.text.toString(),
            totalMoisture = tm,
            inherentMoisture = im,
            equilibrialMoisture = em,
            volatileMatter = etVolatileMatter.text.toString().toDoubleOrNull() ?: 0.0,
            ash = ash,
            gcvAdb = gcvAdb,
            factor = factor,
            gcvArb = gcvArb,
            equilibrialFactor = equilibrialFactor,
            coalGrade = grade
        )
        dataManager.saveProximateAnalysis(data)
    }

    private enum class CoalGrade(val gradeName: String, val colorResId: Int) {
        G1("G1  >7000 kcal/kg",        R.color.grade_g1),
        G2("G2  6701–7000 kcal/kg",    R.color.grade_g2),
        G3("G3  6401–6700 kcal/kg",    R.color.grade_g3),
        G4("G4  6101–6400 kcal/kg",    R.color.grade_g4),
        G5("G5  5801–6100 kcal/kg",    R.color.grade_g5),
        G6("G6  5501–5800 kcal/kg",    R.color.grade_g6),
        G7("G7  5201–5500 kcal/kg",    R.color.grade_g7),
        G8("G8  4901–5200 kcal/kg",    R.color.grade_g8),
        G9("G9  4601–4900 kcal/kg",    R.color.grade_g9),
        G10("G10  4301–4600 kcal/kg",  R.color.grade_g10),
        G11("G11  4001–4300 kcal/kg",  R.color.grade_g11),
        G12("G12  3701–4000 kcal/kg",  R.color.grade_g12),
        G13("G13  3401–3700 kcal/kg",  R.color.grade_g13),
        G14("G14  3101–3400 kcal/kg",  R.color.grade_g14),
        G15("G15  2801–3100 kcal/kg",  R.color.grade_g15),
        G16("G16  2501–2800 kcal/kg",  R.color.grade_g16),
        G17("G17  2201–2500 kcal/kg",  R.color.grade_g17)
    }
}
