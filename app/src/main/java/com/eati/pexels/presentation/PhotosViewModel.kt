package com.eati.pexels.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eati.pexels.data.PhotosRepository
import com.eati.pexels.domain.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhotosViewModel(private val repository: PhotosRepository) : ViewModel() {
    private var selectedElement = -1
    val photosFlow = MutableStateFlow<List<Photo>>(listOf())
    val selectedFlow = MutableStateFlow(selectedElement)
    fun updateResults(query: String) {
        viewModelScope.launch {
            val results = repository.getPhotos(query)
            selectedElement = -1
            selectedFlow.emit(selectedElement)
            photosFlow.emit(results)
        }
    }
    fun onImageClick(element: Int){
        selectedElement = if(element == selectedElement)
            -1
        else{
            element
        }
        viewModelScope.launch{
            selectedFlow.emit(selectedElement)
        }
    }
}