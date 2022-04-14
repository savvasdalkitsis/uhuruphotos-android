package com.savvasdalkitsis.librephotos.home.module

import javax.inject.Qualifier

class HomeModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HomeNavigationTargetFeed
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HomeNavigationTargetSearch

}