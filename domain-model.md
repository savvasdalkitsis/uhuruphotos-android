This page demonstrates the domain model of the app

(still WIP)

```mermaid 
graph TD;

    Galleria-->Gallery
    Gallery-->GalleryGroup
    GalleryGroup-->MediaItem
    Exhibit-->MediaItem
    MediaItem-->LocalMediaItem
    MediaItem-->RemoteMediaItem
    LocalMediaItem-->Image
    LocalMediaItem-->Video
    RemoteMediaItem-->Image
    RemoteMediaItem-->Video
    AutoAlbum-->Galleria
    UserAlbum-->Galleria
    Favourites-->Galleria
    TrashMedia-->Galleria
    LocalMedia-->LocalGallery
    LocalGallery-->Galleria
    HiddenMedia-->Galleria
    Person-->Gallery
    SearchResults-->Gallery
    Feed-->Gallery
    People-->PeopleList
    PeopleList-->Person
    Exhibit-->PeopleList
    AutoAlbum-->PeopleList

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