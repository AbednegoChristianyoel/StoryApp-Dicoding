package com.dicoding.abednego.storyapp.utils

import com.dicoding.abednego.storyapp.data.api.response.ListStoryItem

object DataDummy {
    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 1..5) {
            val story = ListStoryItem(
                id = "story-lD7wCANdnZ6EOA-6",
                name = "abednego",
                description = "halo",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1683436997258_I8qNbMTl.jpg",
                createdAt = "createdAt\":\"2023-05-07T05:23:17.260Z",
                lat = -6.220355,
                lon = 106.86283333333333
            )
            storyList.add(story)
        }
        return storyList
    }
}