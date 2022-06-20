package com.fidoo.user.store.storepagingsource

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fidoo.user.store.model.StoreListingModel
import java.lang.Integer.max
import java.time.LocalDateTime

class StorePagingSource: PagingSource<Int, StoreListingModel>() {

    val STARTING_KEY: Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    private val firstArticleCreatedTime = LocalDateTime.now()

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreListingModel> {
        val start = params.key?: STARTING_KEY
        val range = start.until(start + params.loadSize)

        return LoadResult.Page(
            data = range.map {
                StoreListingModel()
            },
            prevKey = when(start) {
                STARTING_KEY -> null
                else -> ensureValidKey(key = range.first - params.loadSize)
            },
            nextKey = range.last + 1
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getRefreshKey(state: PagingState<Int, StoreListingModel>): Int? {
        return super.getRefreshKey(state)
    }
}