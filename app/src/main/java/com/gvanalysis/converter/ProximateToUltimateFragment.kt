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

class ProximateToUltimateFragment : Fragment() {

    // Proximate inputs
    private lateinit var etFixedCarbon: TextInputEditText
    private lateinit var etAsh: TextInputEditText
    private lateinit var etVolatileMatter: TextInputEditText
    private lateinit var etMoisture: TextInputEditText

    // Ultimate fields (bidirectional: input OR output)
    private lateinit var etPctC: TextInputEditText
    private lateinit var etPctH2: TextInputEditText
    private lateinit var etPctN2: TextInputEditText

    // Layouts for error display
    private lateinit var layoutFixedCarbon: TextInputLayout
    private lateinit var layoutAsh: TextInputLayout
    private lateinit var layoutVolatileMatter: TextInputLayout
    private lateinit var layoutMoisture: TextInputLayout
    private lateinit var layoutPctC: TextInputLayout
    private lateinit var layoutPctH2: TextInputLayout
    private lateinit var layoutPctN2: TextInputLayout

    // Result views
    private lateinit var resultsContainer: LinearLayout
    private lateinit var cardSolvedProximate: View
    private lateinit var tvResultC: TextView
    private lateinit var tvResultVM: TextView
    private lateinit var tvResultPctC: TextView
    private lateinit var tvResultPctH2: TextView
    private lateinit var tvResultPctN2: TextView

    private lateinit var btnCalculate: MaterialButton
    private lateinit var btnClear: MaterialButton

    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_proximate_to_ultimate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupButtons()
    }

    private fun initViews(view: View) {
        etFixedCarbon = view.findViewById(R.id.etFixedCarbon)
        etAsh = view.findViewById(R.id.etAshUlt)
        etVolatileMatter = view.findViewById(R.id.etVolatileMatterUlt)
        etMoisture = view.findViewById(R.id.etMoisture)
        etPctC = view.findViewById(R.id.etPctC)
        etPctH2 = view.findViewById(R.id.etPctH2)
        etPctN2 = view.findViewById(R.id.etPctN2)

        layoutFixedCarbon = view.findViewById(R.id.layoutFixedCarbon)
        layoutAsh = view.findViewById(R.id.layoutAsh)
        layoutVolatileMatter = view.findViewById(R.id.layoutVolatileMatter)
        layoutMoisture = view.findViewById(R.id.layoutMoisture)
        layoutPctC = view.findViewById(R.id.layoutPctC)
        layoutPctH2 = view.findViewById(R.id.layoutPctH2)
        layoutPctN2 = view.findViewById(R.id.layoutPctN2)

        resultsContainer = view.findViewById(R.id.resultsContainerUlt)
        cardSolvedProximate = view.findViewById(R.id.cardSolvedProximate)
        tvResultC = view.findViewById(R.id.tvResultC)
        tvResultVM = view.findViewById(R.id.tvResultVM)
        tvResultPctC = view.findViewById(R.id.tvResultPctC)
        tvResultPctH2 = view.findViewById(R.id.tvResultPctH2)
        tvResultPctN2 = view.findViewById(R.id.tvResultPctN2)

        btnCalculate = view.findViewById(R.id.btnCalculateUlt)
        btnClear = view.findViewById(R.id.btnClearUlt)
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
            etFixedCarbon to layoutFixedCarbon,
            etAsh to layoutAsh,
            etVolatileMatter to layoutVolatileMatter,
            etMoisture to layoutMoisture,
            etPctC to layoutPctC,
            etPctH2 to layoutPctH2,
            etPctN2 to layoutPctN2
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
            layoutFixedCarbon, layoutAsh, layoutVolatileMatter, layoutMoisture,
            layoutPctC, layoutPctH2, layoutPctN2
        ).forEach { it.error = null }
    }

    /**
     * Bidirectional calculation engine.
     *
     * Forward (proximate → ultimate):
     *   %C  = 0.97C + 0.7(VM + 0.1A) − M(0.6 − 0.01M)
     *   %H2 = 0.036C + 0.086(VM − 0.1A) − 0.0035M²(1 − 0.02M)
     *   %N2 = 2.10 − 0.020 × VM
     *
     * Reverse:
     *   VM  from %N2: VM = (2.10 − %N2) / 0.020
     *   C   from %C (given VM, A, M): C = (%C − 0.7(VM+0.1A) + M(0.6−0.01M)) / 0.97
     *   C   from %H2 (given VM, A, M): C = (%H2 − 0.086(VM−0.1A) + 0.0035M²(1−0.02M)) / 0.036
     */
    private fun performCalculation() {
        // Collect known values (null = unknown)
        var c  = etFixedCarbon.text.toString().toDoubleOrNull()
        var a  = etAsh.text.toString().toDoubleOrNull()
        var vm = etVolatileMatter.text.toString().toDoubleOrNull()
        var m  = etMoisture.text.toString().toDoubleOrNull()
        var pctC  = etPctC.text.toString().toDoubleOrNull()
        var pH2   = etPctH2.text.toString().toDoubleOrNull()
        var pN2   = etPctN2.text.toString().toDoubleOrNull()

        var solvedC: Double? = null
        var solvedVM: Double? = null

        // ── Reverse step 1: resolve VM from %N2 ──────────────────────────────
        if (pN2 != null && vm == null) {
            vm = (2.10 - pN2) / 0.020
            solvedVM = vm
        }

        // ── Reverse step 2: resolve C from ultimate outputs ───────────────────
        if (c == null && vm != null && a != null && m != null) {
            when {
                pctC != null -> {
                    c = (pctC - 0.7 * (vm + 0.1 * a) + m * (0.6 - 0.01 * m)) / 0.97
                    solvedC = c
                }
                pH2 != null -> {
                    c = (pH2 - 0.086 * (vm - 0.1 * a) + 0.0035 * m * m * (1 - 0.02 * m)) / 0.036
                    solvedC = c
                }
            }
        }

        // ── Forward: compute all ultimate outputs that are now calculable ─────
        if (c != null && a != null && vm != null && m != null) {
            val calcPctC  = 0.97 * c + 0.7 * (vm + 0.1 * a) - m * (0.6 - 0.01 * m)
            val calcPH2   = 0.036 * c + 0.086 * (vm - 0.1 * a) - 0.0035 * m * m * (1 - 0.02 * m)
            val calcPN2   = 2.10 - 0.020 * vm
            pctC = calcPctC
            pH2  = calcPH2
            pN2  = calcPN2
        } else if (vm != null && pN2 == null) {
            pN2 = 2.10 - 0.020 * vm
        }

        // ── Check that at least something was computed ────────────────────────
        if (pctC == null && pH2 == null && pN2 == null && solvedC == null && solvedVM == null) {
            Toast.makeText(
                requireContext(),
                "Not enough values to calculate. Provide more inputs.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // ── Show solved proximate card only when something was back-calculated ─
        val hasSolvedProximate = (solvedC != null || solvedVM != null)
        cardSolvedProximate.visibility = if (hasSolvedProximate) View.VISIBLE else View.GONE
        tvResultC.text  = if (solvedC  != null) "${fmt(solvedC)} %" else "—"
        tvResultVM.text = if (solvedVM != null) "${fmt(solvedVM)} %" else "—"

        tvResultPctC.text  = if (pctC != null) "${fmt(pctC)} %"  else "—"
        tvResultPctH2.text = if (pH2  != null) "${fmt(pH2)}  %"  else "—"
        tvResultPctN2.text = if (pN2  != null) "${fmt(pN2)}  %"  else "—"

        if (resultsContainer.visibility != View.VISIBLE) {
            resultsContainer.visibility = View.VISIBLE
            resultsContainer.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.slide_up)
            )
        }

        Toast.makeText(requireContext(), "Calculation complete", Toast.LENGTH_SHORT).show()
    }

    private fun clearAll() {
        listOf(etFixedCarbon, etAsh, etVolatileMatter, etMoisture,
               etPctC, etPctH2, etPctN2).forEach { it.text?.clear() }
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
