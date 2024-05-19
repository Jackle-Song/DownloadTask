package com.jacklesong.downloadtask1.activity

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jacklesong.downloadtask1.R
import com.jacklesong.downloadtask1.databinding.ActivityHomeBinding
import com.jacklesong.downloadtask1.fragment.SharedDetailsFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setupComponentListener()
    }

    private fun initUI() {
        binding.imgFirstTab.setColorFilter(
            ContextCompat.getColor(this@HomeActivity,
                R.color.light_green_6E
            ), PorterDuff.Mode.SRC_ATOP)

        loadFragment(SharedDetailsFragment.newInstance(getString(R.string.home_activity_tab_one_title)))
    }

    private fun setupComponentListener() {
        binding.imgFirstTab.setOnClickListener {
            binding.imgFirstTab.setColorFilter(
                ContextCompat.getColor(this@HomeActivity,
                    R.color.light_green_6E
                ), PorterDuff.Mode.SRC_ATOP)
            binding.imgSecondTab.setColorFilter(
                ContextCompat.getColor(this@HomeActivity,
                    R.color.black
                ), PorterDuff.Mode.SRC_ATOP)

            loadFragment(SharedDetailsFragment.newInstance(getString(R.string.home_activity_tab_one_title)))
        }

        binding.imgSecondTab.setOnClickListener {
            binding.imgFirstTab.setColorFilter(
                ContextCompat.getColor(this@HomeActivity,
                    R.color.black
                ), PorterDuff.Mode.SRC_ATOP)
            binding.imgSecondTab.setColorFilter(
                ContextCompat.getColor(this@HomeActivity,
                    R.color.light_green_6E
                ), PorterDuff.Mode.SRC_ATOP)

            loadFragment(SharedDetailsFragment.newInstance(getString(R.string.home_activity_tab_two_title)))
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutDownloadDetails, fragment)
            .commit()
    }
}