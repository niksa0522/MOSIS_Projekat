package com.example.mosis_projekat.screens.rankings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.mosis_projekat.adapters.RankingsPagerAdapter
import com.example.mosis_projekat.adapters.WorkshopPagerAdapter
import com.example.mosis_projekat.databinding.FragmentRankingsMainBinding
import com.example.mosis_projekat.shared_view_models.RankViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class RankingsMainFragment : Fragment() {

    private var _binding: FragmentRankingsMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel:RankViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankingsMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.getRank()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.rank.observe(viewLifecycleOwner){
            (activity as AppCompatActivity).supportActionBar?.title = "Rankings | $it"
        }

        val viewPager = binding.viewPager
        val pagerAdapter = RankingsPagerAdapter(requireActivity())
        viewPager.adapter=pagerAdapter
        viewPager.isUserInputEnabled=false


        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout,viewPager) { tab: TabLayout.Tab, i: Int ->
            tab.text = pagerAdapter.fragmentNames[i]
        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}