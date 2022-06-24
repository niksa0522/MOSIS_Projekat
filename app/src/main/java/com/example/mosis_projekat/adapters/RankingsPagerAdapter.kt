package com.example.mosis_projekat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mosis_projekat.screens.rankingsFriends.RankingsFriendsFragment
import com.example.mosis_projekat.screens.rankingsGlobal.RankingsGlobalFragment
import com.example.mosis_projekat.screens.workshopInfo.WorkshopInfoFragment
import com.example.mosis_projekat.screens.workshopReviews.WorkshopReviewsFragment

class RankingsPagerAdapter(private val fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragments = listOf<Fragment>(RankingsGlobalFragment(), RankingsFriendsFragment())
    public val fragmentNames = listOf<String>("Global","Prijatelji")

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}