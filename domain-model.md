This page demonstrates the domain model of the app

(still WIP)

```mermaid 
graph TD;

    Gallery-->Collage
    Collage-->CollageGroup
    CollageGroup-->MediaItem
    Exhibit-->MediaItem
    MediaItem-->LocalMediaItem
    MediaItem-->RemoteMediaItem
    LocalMediaItem-->Image
    LocalMediaItem-->Video
    RemoteMediaItem-->Image
    RemoteMediaItem-->Video
    AutoAlbum-->Gallery
    UserAlbum-->Gallery
    AutoAlbum-->Vitrine
    UserAlbum-->Vitrine
    Favourites-->Gallery
    Favourites-->Vitrine
    TrashMedia-->Gallery
    LocalMedia-->LocalGallery
    LocalMedia-->Vitrine
    LocalGallery-->Gallery
    HiddenMedia-->Gallery
    Person-->Collage
    SearchResults-->Collage
    Feed-->Collage
    People-->PeopleBanner
    PeopleBanner-->Person
    Exhibit-->PeopleBanner
    AutoAlbum-->PeopleBanner

    subgraph Pages
        Person
        LocalGallery
        Exhibit
    end

    subgraph Search
        SearchResults
        People
    end

    subgraph Albums
        AutoAlbum
        UserAlbum
    end

    subgraph Library
        TrashMedia
        HiddenMedia
        Albums
        Favourites
        LocalMedia
    end

    subgraph Home
        Feed
        Search
        Library
    end
```