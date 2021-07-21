package com.example.moviemvvm.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moviemvvm.data.local.FavoriteMovie
import com.example.moviemvvm.data.remote.model.Movie
import com.example.moviemvvm.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var favoriteViewModel: FavoriteViewModel
    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoriteViewModel =
            ViewModelProvider(this).get(FavoriteViewModel::class.java)

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = FavoriteAdapter()

        favoriteViewModel.movies.observe(viewLifecycleOwner){
            adapter.setMovieList(it)
            binding.apply {
                rvMovie.setHasFixedSize(true)
                rvMovie.adapter = adapter
            }
        }

        adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback{
            override fun onItemClick(favoriteMovie: FavoriteMovie) {
                val movie = Movie(
                    favoriteMovie.id_movie,
                    favoriteMovie.overview,
                    favoriteMovie.poster_path,
                    favoriteMovie.original_title,
                )
                val action = FavoriteFragmentDirections.actionNavFavoriteToNavDetails(movie)
                findNavController().navigate(action)

            }

        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}