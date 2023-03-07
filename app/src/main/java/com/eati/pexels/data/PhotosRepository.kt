package com.eati.pexels.data

import android.util.Log
import com.eati.pexels.domain.Photo
import kotlinx.coroutines.delay
import java.io.IOException

class PhotosRepository {

    private val pexelsApi = PexelsApi.create()

    suspend fun getPhotos(query: String): List<Photo> {
        var ok = false
        var res: List<Photo> = listOf()
        var errors = 0
        while(!ok && errors < 3) {
            try {
                res = pexelsApi.getPhotos(query).photos.map {
                    Photo(
                        id = it.id,
                        width = it.width,
                        height = it.height,
                        url = it.url,
                        photographer = it.photographer,
                        photographerUrl = it.photographerUrl,
                        photographerId = it.photographerId,
                        avgColor = it.avgColor,
                        liked = it.liked,
                        alt = it.alt,
                        sourceURL = it.src.medium
                    )
                }
                ok = true
            } catch (e: IOException) {
                Log.e("Error getting images", e.stackTraceToString())
                errors++
                delay(1000)
            }
        }
        return res
    }

}