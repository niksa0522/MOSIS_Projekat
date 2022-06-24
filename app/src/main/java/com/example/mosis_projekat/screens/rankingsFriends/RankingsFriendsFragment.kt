package com.example.mosis_projekat.screens.rankingsFriends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.R
import com.example.mosis_projekat.adapters.UserRanksListAdapter
import com.example.mosis_projekat.models.UserWithRank
import com.example.mosis_projekat.shared_view_models.RankViewModel


class RankingsFriendsFragment : Fragment(),UserRanksListAdapter.UserClickInterface {

    private val viewModel: RankViewModel by activityViewModels()
    private lateinit var adapter: UserRanksListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel.getFriendRanks()
        return inflater.inflate(R.layout.fragment_ranking_friends, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerWorkshops = view.findViewById<RecyclerView>(R.id.recyclerUsers)
        viewModel.friendRanks.observe(viewLifecycleOwner){
            var list = mutableListOf<UserWithRank>()
            list.addAll(it)
            list.reverse()
            adapter = UserRanksListAdapter(list,this)
            recyclerWorkshops.adapter = adapter
        }
        recyclerWorkshops.layoutManager= LinearLayoutManager(requireContext())
        recyclerWorkshops.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        )
        )
    }

    override fun onItemClicked(uid: String) {
        val bundle = Bundle()
        bundle.putString("uid",uid)
        findNavController().navigate(R.id.action_rankingsMainFragment_to_viewFriendFragment,bundle)
    }

}