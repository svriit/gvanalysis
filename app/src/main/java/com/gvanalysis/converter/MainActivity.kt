package com.gvanalysis.converter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_proximate)
                1 -> getString(R.string.tab_prox_to_ult)
                2 -> getString(R.string.tab_total_moisture)
                3 -> getString(R.string.tab_coal)
                4 -> getString(R.string.tab_heat)
                else -> ""
            }
        }.attach()
    }

    private inner class ViewPagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ProximateAnalysisFragment()
                1 -> ProximateToUltimateFragment()
                2 -> TotalMoistureFragment()
                3 -> CoalAnalysisFragment()
                4 -> HeatValueFragment()
                else -> ProximateAnalysisFragment()
            }
        }
    }
}
