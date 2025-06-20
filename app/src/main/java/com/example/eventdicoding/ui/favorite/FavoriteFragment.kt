package com.example.eventdicoding.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventdicoding.databinding.FragmentFavoriteBinding
import com.example.eventdicoding.ui.adapter.ListItemAdapter
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory

class FavoriteFragment :  Fragment() {
    private  var _binding: FragmentFavoriteBinding? = null
    private  val binding get() = _binding!!
    private lateinit var  progressBar: ProgressBar
    private  lateinit var  listRecyclerView: RecyclerView
    private lateinit var  listAdapter: ListItemAdapter
    private  val viewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.progressBar
        listRecyclerView = binding.favoriteListRecyclerView
        setupRecyclerView()
        observeFavoriteEvents()
    }
    private  fun setupRecyclerView(){
        listAdapter = ListItemAdapter(viewModel)
        listRecyclerView.adapter = listAdapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private  fun observeFavoriteEvents(){
        viewModel.fetchFavoritedEvents().observe(viewLifecycleOwner){result ->
            listAdapter.setEvents(result)
        }
    }

    override fun onResume() {
        super.onResume()
        observeFavoriteEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}