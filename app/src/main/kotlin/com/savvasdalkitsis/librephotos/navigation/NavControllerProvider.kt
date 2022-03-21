package com.savvasdalkitsis.librephotos.navigation

import androidx.navigation.NavHostController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavControllerProvider @Inject constructor() {

    var navController: NavHostController? = null

}
