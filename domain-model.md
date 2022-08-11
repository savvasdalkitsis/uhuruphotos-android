This page demonstrates the domain model of the app

(still WIP)

```mermaid
graph TD;
    Gallery-->Feed;
    Feed-->FeedGroup;
    FeedGroup-->MediaItem;
    MediaItem-->LocalMediaItem;
    MediaItem-->RemoteMediaItem;
    AutoAlbum-->Album;
    UserAlbum-->Album;
    Album-->Gallery;
    Favourites-->Gallery;
    TrashMedia-->Gallery;
    LocalMedia-->Gallery;
    HiddenMedia-->Gallery;
    
    subgraph Local
        LocalMediaItem;
        LocalMedia;
    end
    
    subgraph Remote
        RemoteMediaItem;
        AutoAlbum;
        UserAlbum;
        Album;
        Favourites;
        TrashMedia;
        HiddenMedia;
    end
```