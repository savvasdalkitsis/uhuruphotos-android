package com.savvasdalkitsis.librephotos.photos.view.state

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.ui.text.AnnotatedString
import com.google.android.gms.maps.model.LatLng

@ExperimentalMaterialApi
data class PhotoState(
    val id: String = "",
    val isLoading: Boolean = false,
    val fullResUrl: String = "",
    val lowResUrl: String = "",
    val errorMessage: String? = null,
    val showUI: Boolean = true,
    val showRefresh: Boolean = false,
    val showInfoButton: Boolean = false,
    val isFavourite: Boolean? = null,
    val infoSheetState: ModalBottomSheetValue = ModalBottomSheetValue.Hidden,
    val dateAndTime: String = "",
    val location: String = "",
    val gps: LatLng? = null,
)