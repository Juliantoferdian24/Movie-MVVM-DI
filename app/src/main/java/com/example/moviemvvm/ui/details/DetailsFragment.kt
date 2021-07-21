package com.example.moviemvvm.ui.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.moviemvvm.R
import com.example.moviemvvm.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private lateinit var detailsViewModel: DetailsViewModel
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailsFragmentArgs>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        detailsViewModel =
            ViewModelProvider(this).get(DetailsViewModel::class.java)
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            val movie = args.movie
            Glide.with(this@DetailsFragment)
                .load("${movie.baseUrl}${movie.poster_path}")
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        tvDescription.isVisible = true
                        tvMovieTitle.isVisible = true
                        return false
                    }
                }).into(ivMoviePoster)

            var isChecked = false

            CoroutineScope(Dispatchers.IO).launch {
                val count = detailsViewModel.checkMovie(movie.id)
                withContext(Main){
                    if(count>0){
                        toggleFavorite.isChecked = true
                        isChecked = true
                    } else{
                        toggleFavorite.isChecked = false
                        isChecked = false
                    }
                }
            }
            tvDescription.text = movie.overview
            tvMovieTitle.text = movie.original_title

            toggleFavorite.setOnClickListener {
                isChecked = !isChecked
                if(isChecked){
                    detailsViewModel.addToFavorite(movie)
                    Toast.makeText(context, "fav", Toast.LENGTH_SHORT).show()
                } else{
                    detailsViewModel.removeFromFavorite(movie.id)
                    Toast.makeText(context, "unfav", Toast.LENGTH_SHORT).show()
                }
                toggleFavorite.isChecked = isChecked
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}