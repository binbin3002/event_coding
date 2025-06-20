package com.example.eventdicoding.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventdicoding.databinding.FragmentHomeBinding
import com.example.eventdicoding.ui.adapter.GridItemAdapter
import com.example.eventdicoding.ui.adapter.ListItemAdapter
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory
import com.example.eventdicoding.data.Result

class HomeFragment : Fragment() {
    private  var _binding: FragmentHomeBinding? = null
    private  val binding get() = _binding!!
    private lateinit var  gridRecyclerView: RecyclerView
    private lateinit var listRecyclerView: RecyclerView
    private  lateinit var gridAdapter: GridItemAdapter
    private  lateinit var listAdapter: ListItemAdapter
    private  lateinit var progressBar: ProgressBar
    private lateinit var  progressBarFinishe: ProgressBar
    private  lateinit var  pref: SettingPreferences
    private  val viewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext(), pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridRecyclerView = binding.gridRecyclerView
        listRecyclerView = binding.listRecyclerView
        progressBarFinishe = binding.progressBarFinishe
        progressBar = binding.progressBar

        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        gridAdapter = GridItemAdapter(viewModel)
        listAdapter = ListItemAdapter(viewModel)

        gridRecyclerView.adapter = gridAdapter
        listRecyclerView.adapter = listAdapter

        gridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        observeGridRecycler()
        observeListRecycler()
    }
    private  fun observeGridRecycler(){
        viewModel.fetchActiveEvents().observe(viewLifecycleOwner){result ->
            if (result != null){
                when (result){
                    is Result.Loading ->{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success ->{
                        binding.progressBar.visibility = View.GONE
                        gridAdapter.setEvents(result.data.take(5))
                    }
                    is Result.Error ->{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context,
                            "Error:" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

    private  fun observeListRecycler(){
        viewModel.fetchInactiveEvents().observe(viewLifecycleOwner){result ->
            if (result != null){
                when (result){
                    is Result.Loading ->{
                        binding.progressBarFinishe.visibility = View.VISIBLE
                    }
                    is Result.Success ->{
                        binding.progressBarFinishe.visibility = View.GONE
                        listAdapter.setEvents(result.data.take(5))
                    }
                    is Result.Error ->{
                        binding.progressBarFinishe.visibility = View.GONE

                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}