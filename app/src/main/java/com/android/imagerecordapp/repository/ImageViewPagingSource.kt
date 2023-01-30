package com.android.imagerecordapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.android.imagerecordapp.data.ImageViewData
import com.android.imagerecordapp.data.ImageViewDatabase
import java.io.IOException

class ImageViewPagingSource: PagingSource<Int, ImageViewData>() {
    override fun getRefreshKey(state: PagingState<Int, ImageViewData>): Int? {
        println("getRefreshKey $state")
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageViewData> {
        val page = params.key ?: STARTING_PAGE
        return try {
            var data: List<ImageViewData>? = null

            ImageViewDatabase.imageViewDB!!.withTransaction {
                data = ImageViewDatabase.imageViewDB!!.imageViewDao().getAll(page)
            }

            LoadResult.Page(
                data = data!!,
                prevKey = if (page == STARTING_PAGE) null else page-1,
                nextKey = if(data.isNullOrEmpty()) null else page+1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

private const val STARTING_PAGE = 1 // 초기 페이지 상수 값