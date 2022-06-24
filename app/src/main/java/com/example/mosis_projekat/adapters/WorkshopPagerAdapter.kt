package com.example.mosis_projekat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mosis_projekat.screens.workshopInfo.WorkshopInfoFragment
import com.example.mosis_projekat.screens.workshopReviews.WorkshopReviewsFragment

class WorkshopPagerAdapter(private val fa:FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragments = listOf<Fragment>(WorkshopInfoFragment(),WorkshopReviewsFragment())
    public val fragmentNames = listOf<String>("Info","Ocene")

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}