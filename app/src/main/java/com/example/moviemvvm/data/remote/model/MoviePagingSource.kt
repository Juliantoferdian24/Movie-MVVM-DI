package com.example.moviemvvm.data.remote.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException


class MoviePagingSource(private val movieApi: MovieApi, private val query: String?) :
    PagingSource<Int, Movie>() {
    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val position = params.key ?: Companion.STARTING_PAGE_INDEX
            val response = if (query != null) movieApi.searchMovies(
                query,
                position
            ) else movieApi.getNowPlayingMovies(position)
            val movies = response.results

            LoadResult.Page(
                data = movies,
                prevKey = if (position == Companion.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}