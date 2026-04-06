package com.gvanalysis.converter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 3000L   // total ms before launching MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide action bar for full-screen feel
        supportActionBar?.hide()

        setContentView(R.layout.activity_splash)

        val logo      = findViewById<ImageView>(R.id.splashLogo)
        val glow      = findViewById<View>(R.id.splashGlow)
        val appName   = findViewById<TextView>(R.id.splashAppName)
        val tagline   = findViewById<TextView>(R.id.splashTagline)
        val dots      = findViewById<LinearLayout>(R.id.splashDots)
        val dot1      = findViewById<View>(R.id.dot1)
        val dot2      = findViewById<View>(R.id.dot2)
        val dot3      = findViewById<View>(R.id.dot3)
        val credit    = findViewById<TextView>(R.id.splashCredit)

        animateSplash(logo, glow, appName, tagline, dots, dot1, dot2, dot3, credit)
    }

    private fun animateSplash(
        logo: ImageView, glow: View,
        appName: TextView, tagline: TextView,
        dots: LinearLayout, dot1: View, dot2: View, dot3: View,
        credit: TextView
    ) {
        // ── 1. Glow pulse: fade in behind logo ───────────────────────────────
        ObjectAnimator.ofFloat(glow, "alpha", 0f, 0.9f).apply {
            duration = 600
            startDelay = 100
            interpolator = DecelerateInterpolator()
            start()
        }

        // ── 2. Logo: bounce in from scale=0 ──────────────────────────────────
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1.15f, 1f),
                ObjectAnimator.ofFloat(logo, "scaleY", 0f, 1.15f, 1f),
                ObjectAnimator.ofFloat(logo, "alpha",  0f, 1f)
            )
            duration = 750
            startDelay = 200
            interpolator = OvershootInterpolator(1.8f)
            start()
        }

        // ── 3. Flame flicker: subtle scale oscillation after logo lands ───────
        val flickerX = ValueAnimator.ofFloat(1f, 1.04f, 0.97f, 1.02f, 0.98f, 1f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            startDelay = 950
            addUpdateListener { logo.scaleX = it.animatedValue as Float }
        }
        val flickerY = ValueAnimator.ofFloat(1f, 1.06f, 0.96f, 1.03f, 0.97f, 1f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            startDelay = 950
            addUpdateListener { logo.scaleY = it.animatedValue as Float }
        }
        flickerX.start()
        flickerY.start()

        // ── 4. Glow pulse synced with flicker ─────────────────────────────────
        ValueAnimator.ofFloat(0.9f, 1.3f, 0.9f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            startDelay = 950
            addUpdateListener { glow.scaleX = it.animatedValue as Float
                                glow.scaleY = it.animatedValue as Float }
            start()
        }

        // ── 5. App name: slide up + fade in ──────────────────────────────────
        appName.translationY = 60f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(appName, "translationY", 60f, 0f),
                ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f)
            )
            duration = 500
            startDelay = 800
            interpolator = DecelerateInterpolator()
            start()
        }

        // ── 6. Tagline: slide up + fade in ───────────────────────────────────
        tagline.translationY = 40f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(tagline, "translationY", 40f, 0f),
                ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f)
            )
            duration = 400
            startDelay = 1050
            interpolator = DecelerateInterpolator()
            start()
        }

        // ── 7. Loading dots: fade in then animate sequentially ────────────────
        ObjectAnimator.ofFloat(dots, "alpha", 0f, 1f).apply {
            duration = 300
            startDelay = 1300
            start()
        }
        animateDots(dot1, dot2, dot3, dotsStartDelay = 1600)

        // ── 8. Credit: fade in ────────────────────────────────────────────────
        ObjectAnimator.ofFloat(credit, "alpha", 0f, 0.6f).apply {
            duration = 400
            startDelay = 1400
            start()
        }

        // ── 9. Launch MainActivity ────────────────────────────────────────────
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_DURATION)
    }

    /** Sequentially pulses three dots to form a loading wave. */
    private fun animateDots(dot1: View, dot2: View, dot3: View, dotsStartDelay: Long) {
        val period = 500L
        val pulseDuration = 350L

        fun pulseDot(dot: View, delayMs: Long) {
            val anim = ValueAnimator.ofFloat(0.4f, 1f, 0.4f)
            anim.duration = pulseDuration
            anim.startDelay = delayMs
            anim.repeatCount = ValueAnimator.INFINITE
            anim.repeatMode = ValueAnimator.RESTART
            anim.addUpdateListener { dot.alpha = it.animatedValue as Float }
            anim.start()
        }

        pulseDot(dot1, dotsStartDelay)
        pulseDot(dot2, dotsStartDelay + period)
        pulseDot(dot3, dotsStartDelay + period * 2)
    }
}
