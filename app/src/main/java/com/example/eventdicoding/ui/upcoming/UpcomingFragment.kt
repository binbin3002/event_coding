package com.example.eventdicoding.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.data.Result
import androidx.recyclerview.widget.RecyclerView
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.databinding.FragmentUpcomingBinding
import com.example.eventdicoding.ui.adapter.ListItemAdapter
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory

class UpcomingFragment : Fragment() {
    private  var _binding : FragmentUpcomingBinding? = null
    private  val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    private  lateinit var  progressBar: ProgressBar
    private  lateinit var listRecyclerView: RecyclerView
    private lateinit var  listAdapter: ListItemAdapter
    private  lateinit var  searchViewUpcoming: SearchView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpcomingBinding.inflate(inflater,container,false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewUpcoming = binding.searchViewUpcoming
        progressBar = binding.progressBar
        listRecyclerView = binding.upcomingListRecyclerView

        searchViewUpcoming.setQuery(null, false)
        searchViewUpcoming.clearFocus()
        searchViewUpcoming.isIconified = true

        setupSearchView()
        setupRecyclerView()
        observeActiveEvents()
    }
    private  fun setupSearchView(){
        searchViewUpcoming.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
               doSearch(query)
               return  true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()){
                    observeActiveEvents()
                }
                return true
            }
        })
    }

    private  fun setupRecyclerView(){
        listAdapter  = ListItemAdapter(viewModel)
        listRecyclerView.adapter = listAdapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    private  fun observeActiveEvents(){
        viewModel.fetchActiveEvents().observe(viewLifecycleOwner){result ->
            resultHandler(result)
        }
    }

    private  fun doSearch(query: String){
        val active  = 1
        viewModel.searchEvents(active,query).observe(viewLifecycleOwner){result ->
            resultHandler(result)
        }
    }
    private  fun resultHandler(result: Result<List<EventEntity>>){
        when( result){
            is Result.Loading ->{
                progressBar.visibility = View.VISIBLE
            }
            is Result.Success->{
                progressBar.visibility = View.GONE
                if (result.data.isEmpty()){
                    Toast.makeText(context,"No data Found", Toast.LENGTH_SHORT).show()
                }
                listAdapter.setEvents(result.data)
            }
            is Result.Error -> {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}