package com.adityafakhri.storyapp

import com.adityafakhri.storyapp.data.source.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {

        val storyList: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                "$i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "author $i",
                "description $i",
                0.0.toString(),
                0.0.toString(),
            )
            storyList.add(story)
        }
        return storyList
    }
}