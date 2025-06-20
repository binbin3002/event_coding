package com.example.eventdicoding.ui.finished

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import  com.example.eventdicoding.data.Result
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.databinding.FragmentFinishedBinding
import com.example.eventdicoding.ui.adapter.ListItemAdapter
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory

class FinishedFragment : Fragment() {
    private  var _binding: FragmentFinishedBinding? = null
    private  val binding get() = _binding!!
    private  lateinit var progressBar: ProgressBar
    private lateinit var  listRecyclerView: RecyclerView
    private  lateinit var  lisAdapter: ListItemAdapter
    private  lateinit var  searchViewFinished: SearchView
    private  val viewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewFinished = binding.searchViewFinished
        progressBar = binding.progressBar
        listRecyclerView = binding.finishedListRecyclerView

        searchViewFinished.setQuery(null, false)
        searchViewFinished.clearFocus()
        searchViewFinished.isIconified = true

        setupSearchView()
        setupRecyclerView()
        observeInactiveEvents()
    }
    private  fun setupSearchView(){
        searchViewFinished.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                doSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()){
                    observeInactiveEvents()
                }
                return  true
            }

        })
    }

    private  fun setupRecyclerView(){
        lisAdapter = ListItemAdapter(viewModel)
        listRecyclerView.adapter = lisAdapter
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    private  fun observeInactiveEvents() {
        Log.d("Finishedfragemtn", "observeInactiveEvents called")
        viewModel.fetchInactiveEvents().observe(viewLifecycleOwner) { result ->
            resultHandler(result)
        }
    }

    private  fun doSearch(query: String){
        Log.d("Finishedfragment","dosearch called")
        val active  = 0
        viewModel.searchEvents(active, query).observe(viewLifecycleOwner){result ->
            resultHandler(result)
        }
    }
      private  fun resultHandler(result: Result<List<EventEntity>>){

            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE

                }

                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    if (result.data.isEmpty()) {
                        Toast.makeText(context, "No found data", Toast.LENGTH_SHORT).show()
                    }
                    lisAdapter.setEvents(result.data)
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