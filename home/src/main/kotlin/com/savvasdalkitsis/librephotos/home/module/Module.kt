package com.savvasdalkitsis.librephotos.home.module

import javax.inject.Qualifier

class Module {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HomeNavigationTargetFeed
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HomeNavigationTargetSearch

}