package com.example.taskmaster.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashDelay: Long = 2500 // 2.5 secondes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Masquer la barre d'action
        supportActionBar?.hide()

        // Animation optionnelle
        animateLogo()

        // Délai avant de passer à MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            // Animation de transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, splashDelay)
    }

    private fun animateLogo() {
        // Animation de scale (zoom in)
        binding.ivLogo.apply {
            scaleX = 0f
            scaleY = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .start()
        }

        // Animation de fade in pour le texte
        binding.tvAppName.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(400)
                .start()
        }

        binding.tvTagline.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(600)
                .start()
        }
    }
}
