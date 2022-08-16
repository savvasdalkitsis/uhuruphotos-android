This page demonstrates the domain model of the app

(still WIP)

```mermaid 
graph TD;

    Showroom-->Gallery
    Gallery-->GalleryGroup
    GalleryGroup-->MediaItem
    MediaPage-->MediaItem
    MediaItem-->LocalMediaItem
    MediaItem-->RemoteMediaItem
    LocalMediaItem-->Image
    LocalMediaItem-->Video
    RemoteMediaItem-->Image
    RemoteMediaItem-->Video
    AutoAlbum-->Showroom
    UserAlbum-->Showroom
    Favourites-->Showroom
    TrashMedia-->Showroom
    LocalMedia-->LocalGallery
    LocalGallery-->Showroom
    HiddenMedia-->Showroom
    Person-->Gallery
    SearchResults-->Gallery
    Feed-->Gallery
    People-->PeopleList
    PeopleList-->Person
    MediaPage-->PeopleList
    AutoAlbum-->PeopleList

    subgraph Pages
        Person
        LocalGallery
        MediaPage
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