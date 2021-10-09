package com.example.youtubetest

import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeInfoAPI {
    @GET("videos")
    suspend fun getVideoInfo(
        @Query("id")id:String,
        @Query("key")key:String,
        @Query("part")part:String = "snippet,contentDetails",
        @Query("field")fields : String = "items(id,snippet(title,channelTitle,description,thumbnails(high(url))),contentDetails(duration))"
    ) : YoutubeRetrofitEntity
}