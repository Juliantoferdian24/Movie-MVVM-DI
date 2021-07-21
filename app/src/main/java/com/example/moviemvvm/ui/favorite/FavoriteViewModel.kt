package com.example.moviemvvm.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviemvvm.data.local.FavoriteMovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository: FavoriteMovieRepository): ViewModel() {
    val movies = repository.getFavoriteMovies()
}