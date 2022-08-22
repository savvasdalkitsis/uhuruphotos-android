This page demonstrates the domain model of the app

(still WIP)

```mermaid 
graph TD;

    Gallery-->Collage
    Collage-->CollageGroup
    CollageGroup-->MediaItem
    Lightbox-->MediaItem
    MediaItem-->LocalMediaItem
    MediaItem-->RemoteMediaItem
    LocalMediaItem-->Image
    LocalMediaItem-->Video
    RemoteMediaItem-->Image
    RemoteMediaItem-->Video
    AutoAlbum-->Vitrine
    UserAlbum-->Vitrine
    Favourites-->Vitrine
    Vitrine-->Gallery
    TrashMedia-->Gallery
    LocalMedia-->Vitrine
    LocalGallery-->Gallery
    HiddenMedia-->Gallery
    Person-->Collage
    SearchResults-->Collage
    Feed-->Collage
    People-->PeopleBanner
    PeopleBanner-->Person
    Lightbox-->PeopleBanner
    AutoAlbum-->PeopleBanner

    subgraph Pages
        Person
        LocalGallery
        Lightbox
    end

    subgraph Search
        SearchResults
        People
    end

    subgraph Catalogue
        AutoAlbum
        UserAlbum
    end

    subgraph Library
        TrashMedia
        HiddenMedia
        Catalogue
        Favourites
        LocalMedia
    end

    subgraph Home
        Feed
        Search
        Library
    end
```