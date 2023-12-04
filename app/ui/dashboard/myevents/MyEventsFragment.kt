package com.ibm.internship.beabee.ui.dashboard.myevents

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ibm.internship.beabee.R
import com.ibm.internship.beabee.base.BaseFragment
import com.ibm.internship.beabee.databinding.FragmentMyEventsBinding
import com.ibm.internship.beabee.ui.dashboard.myevents.helpingothers.HelpingOthersFragment
import com.ibm.internship.beabee.ui.dashboard.myevents.myrequests.MyRequestsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyEventsFragment : BaseFragment<FragmentMyEventsBinding>(FragmentMyEventsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagerAdapter()
    }

    private fun setupViewPagerAdapter() {
        val adapter = setupEventsAdapter()
        val viewPager = setUpViewPager(adapter)
        setupTabLayout(adapter, viewPager)
    }

    private fun setupEventsAdapter() = EventsAdapter(requireActivity()).apply {
        addFragment(MyRequestsFragment(), R.string.my_requests_title)
        addFragment(HelpingOthersFragment(), R.string.helping_others_title)
    }

    private fun setUpViewPager(adapter: EventsAdapter): ViewPager2 {
        with(binding) {
            val viewPager = eventsViewPager
            eventsViewPager.adapter = adapter
            return viewPager
        }
    }

    private fun setupTabLayout(adapter: EventsAdapter, viewPager: ViewPager2) {
        val tabs: TabLayout = binding.tabLayout
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = getString(adapter.getTabTitle(position))
        }.attach()
    }
}