package com.example.moviemvvm.ui.movie

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.moviemvvm.R
import com.example.moviemvvm.data.remote.model.Movie
import com.example.moviemvvm.databinding.FragmentMovieBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment(), MovieAdapter.OnItemClickListener{

    private lateinit var movieViewModel: MovieViewModel
    private var _binding: FragmentMovieBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        movieViewModel =
            ViewModelProvider(this).get(MovieViewModel::class.java)

        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = MovieAdapter(this)
        binding.apply {
            rvMovie.setHasFixedSize(true)
            rvMovie.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter { adapter.retry() },
                footer = MovieLoadStateAdapter { adapter.retry() },
            )
            btnTryAgain.setOnClickListener{
                adapter.retry()
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                rvMovie.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnTryAgain.isVisible = loadState.source.refresh is LoadState.Error
                tvFailed.isVisible = loadState.source.refresh is LoadState.Error

                //not found
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                    rvMovie.isVisible = false
                    tvNotFound.isVisible = true
                } else{
                    tvNotFound.isVisible = false
                }
            }
        }

        movieViewModel.movies.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.rvMovie.scrollToPosition(0)
                    movieViewModel.searchMovies(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onItemClick(movie: Movie) {
        val action = MovieFragmentDirections.actionNavMovieToNavDetails(movie)
        findNavController().navigate(action)
    }
}