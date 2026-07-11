package com.demmagence.mories.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.domain.model.TvShow

class TvPagingSource(
    private val api: TmdbApiService,
    private val fetchTvShows: suspend (TmdbApiService, Int) -> List<TvShow>
) : PagingSource<Int, TvShow>() {

    override fun getRefreshKey(state: PagingState<Int, TvShow>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvShow> {
        val page = params.key ?: 1
        return try {
            val tvShows = fetchTvShows(api, page)
            LoadResult.Page(
                data = tvShows,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (tvShows.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
