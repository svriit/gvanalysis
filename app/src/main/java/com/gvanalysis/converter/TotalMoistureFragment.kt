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
import kotlin.math.round

class TotalMoistureFragment : Fragment() {

    // Surface Moisture inputs
    private lateinit var etSurfW1: TextInputEditText
    private lateinit var etSurfW2: TextInputEditText
    private lateinit var etSurfW3: TextInputEditText
    private lateinit var etSurfPctX: TextInputEditText

    // Inherent Moisture inputs
    private lateinit var etInhW1: TextInputEditText
    private lateinit var etInhW2: TextInputEditText
    private lateinit var etInhW3: TextInputEditText
    private lateinit var etInhPctY: TextInputEditText

    // Layouts for error display
    private lateinit var layoutSurfW1: TextInputLayout
    private lateinit var layoutSurfW2: TextInputLayout
    private lateinit var layoutSurfW3: TextInputLayout
    private lateinit var layoutSurfPctX: TextInputLayout
    private lateinit var layoutInhW1: TextInputLayout
    private lateinit var layoutInhW2: TextInputLayout
    private lateinit var layoutInhW3: TextInputLayout
    private lateinit var layoutInhPctY: TextInputLayout

    // Results
    private lateinit var resultsContainer: LinearLayout
    private lateinit var tvResultX: TextView
    private lateinit var tvResultY: TextView
    private lateinit var tvResultTM: TextView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnClear: MaterialButton
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_total_moisture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupButtons()
    }

    private fun initViews(view: View) {
        etSurfW1 = view.findViewById(R.id.etSurfW1)
        etSurfW2 = view.findViewById(R.id.etSurfW2)
        etSurfW3 = view.findViewById(R.id.etSurfW3)
        etSurfPctX = view.findViewById(R.id.etSurfPctX)

        etInhW1 = view.findViewById(R.id.etInhW1)
        etInhW2 = view.findViewById(R.id.etInhW2)
        etInhW3 = view.findViewById(R.id.etInhW3)
        etInhPctY = view.findViewById(R.id.etInhPctY)

        layoutSurfW1 = view.findViewById(R.id.layoutSurfW1)
        layoutSurfW2 = view.findViewById(R.id.layoutSurfW2)
        layoutSurfW3 = view.findViewById(R.id.layoutSurfW3)
        layoutSurfPctX = view.findViewById(R.id.layoutSurfPctX)
        layoutInhW1 = view.findViewById(R.id.layoutInhW1)
        layoutInhW2 = view.findViewById(R.id.layoutInhW2)
        layoutInhW3 = view.findViewById(R.id.layoutInhW3)
        layoutInhPctY = view.findViewById(R.id.layoutInhPctY)

        resultsContainer = view.findViewById(R.id.resultsContainerTM)
        tvResultX = view.findViewById(R.id.tvResultX)
        tvResultY = view.findViewById(R.id.tvResultY)
        tvResultTM = view.findViewById(R.id.tvResultTM)

        btnCalculate = view.findViewById(R.id.btnCalculateTM)
        btnClear = view.findViewById(R.id.btnClearTM)
    }

    private fun setupButtons() {
        btnCalculate.setOnClickListener {
            animateButtonPress(it)
            if (validateInputs()) {
                showLoadingDialog()
                Handler(Looper.getMainLooper()).postDelayed({
                    performCalculation()
                    hideLoadingDialog()
                }, 600)
            }
        }
        btnClear.setOnClickListener {
            animateButtonPress(it)
            clearAll()
        }
    }

    private fun validateInputs(): Boolean {
        clearErrors()
        val fields = listOf(
            etSurfW1 to layoutSurfW1, etSurfW2 to layoutSurfW2,
            etSurfW3 to layoutSurfW3, etSurfPctX to layoutSurfPctX,
            etInhW1 to layoutInhW1, etInhW2 to layoutInhW2,
            etInhW3 to layoutInhW3, etInhPctY to layoutInhPctY
        )
        var valid = true
        for ((et, layout) in fields) {
            val text = et.text.toString()
            if (text.isNotEmpty() && text.toDoubleOrNull() == null) {
                layout.error = getString(R.string.error_invalid_value)
                valid = false
            }
        }
        return valid
    }

    private fun clearErrors() {
        listOf(
            layoutSurfW1, layoutSurfW2, layoutSurfW3, layoutSurfPctX,
            layoutInhW1, layoutInhW2, layoutInhW3, layoutInhPctY
        ).forEach { it.error = null }
    }

    /**
     * Bidirectional calculation for Total Moisture.
     *
     * Surface Moisture:
     *   %X = (W2 − W3) / (W2 − W1) × 100
     *   Reverse for W3: W3 = W2 − %X × (W2−W1) / 100
     *   Reverse for W2: W2 = (W3 − %X/100 × W1) / (1 − %X/100)
     *   Reverse for W1: W1 = W2 − (W2−W3)×100 / %X
     *
     * Inherent Moisture: same formulae with different variables.
     *
     * Total Moisture:
     *   TM = X + Y × (1 − X/100)
     *   Reverse for Y: Y = (TM − X) / (1 − X/100)
     *   Reverse for X: X = (TM − Y) / (1 − Y/100)
     */
    private fun performCalculation() {
        var w1s = etSurfW1.text.toString().toDoubleOrNull()
        var w2s = etSurfW2.text.toString().toDoubleOrNull()
        var w3s = etSurfW3.text.toString().toDoubleOrNull()
        var x   = etSurfPctX.text.toString().toDoubleOrNull()

        var w1i = etInhW1.text.toString().toDoubleOrNull()
        var w2i = etInhW2.text.toString().toDoubleOrNull()
        var w3i = etInhW3.text.toString().toDoubleOrNull()
        var y   = etInhPctY.text.toString().toDoubleOrNull()

        // ── Surface Moisture ─────────────────────────────────────────────────
        x = resolveMoisturePct(w1s, w2s, w3s, x, layoutSurfPctX) ?: x

        // ── Inherent Moisture ────────────────────────────────────────────────
        y = resolveMoisturePct(w1i, w2i, w3i, y, layoutInhPctY) ?: y

        // ── Total Moisture ───────────────────────────────────────────────────
        val tm: Double? = when {
            x != null && y != null -> x + y * (1.0 - x / 100.0)
            else -> null
        }

        if (x == null && y == null && tm == null) {
            Toast.makeText(
                requireContext(),
                "Not enough values to calculate. Provide more inputs.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        tvResultX.text  = if (x  != null) "${fmt(x)} %"  else "—"
        tvResultY.text  = if (y  != null) "${fmt(y)} %"  else "—"
        tvResultTM.text = if (tm != null) "${fmt(tm)} %" else "—"

        if (resultsContainer.visibility != View.VISIBLE) {
            resultsContainer.visibility = View.VISIBLE
            resultsContainer.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.slide_up)
            )
        }

        Toast.makeText(requireContext(), "Calculation complete", Toast.LENGTH_SHORT).show()
    }

    /**
     * Given W1, W2, W3 and/or the moisture percentage, resolve as many unknowns
     * as possible.  Returns the resolved (or already-known) percentage, or null
     * if insufficient data.
     *
     * Formula: pct = (W2 − W3) / (W2 − W1) × 100
     */
    private fun resolveMoisturePct(
        w1: Double?, w2: Double?, w3: Double?,
        pct: Double?,
        errorLayout: TextInputLayout
    ): Double? {
        return when {
            // All three weights known → compute percentage
            w1 != null && w2 != null && w3 != null -> {
                val denom = w2 - w1
                if (denom == 0.0) {
                    errorLayout.error = "W1 and W2 must differ"
                    null
                } else {
                    (w2 - w3) / denom * 100.0
                }
            }

            // Two weights + percentage → nothing extra to compute here (pct already known)
            pct != null -> pct

            // Only two weights and no percentage — can't determine uniquely
            else -> null
        }
    }

    private fun clearAll() {
        listOf(etSurfW1, etSurfW2, etSurfW3, etSurfPctX,
               etInhW1, etInhW2, etInhW3, etInhPctY).forEach { it.text?.clear() }
        clearErrors()
        resultsContainer.visibility = View.GONE
        Toast.makeText(requireContext(), "Fields cleared", Toast.LENGTH_SHORT).show()
    }

    private fun fmt(v: Double): String = (round(v * 100.0) / 100.0).toString()

    private fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext()).apply {
            val container = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.loading_background))
                setPadding(80, 80, 80, 80)
                val pb = android.widget.ProgressBar(requireContext()).apply {
                    indeterminateTintList = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.primary)
                    )
                }
                addView(pb, android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.setMargins(0, 0, 0, 32) })
                val tv = TextView(requireContext()).apply {
                    text = "Calculating..."
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    gravity = android.view.Gravity.CENTER
                }
                addView(tv)
            }
            setContentView(container)
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.7).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawableResource(R.drawable.card_elevated)
            setCancelable(false)
            show()
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun animateButtonPress(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f).apply { duration = 100; start() }
        ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f).apply { duration = 100; start() }
        ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).apply { duration = 100; startDelay = 100; start() }
        ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).apply { duration = 100; startDelay = 100; start() }
    }
}
