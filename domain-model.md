This page demonstrates the domain model of the app

(still WIP)

```mermaidgraph TD;
    Gallery-->Feed;
    Feed-->FeedGroup;
    FeedGroup-->MediaItem;
    MediaPage-->MediaItem;
    MediaItem-->LocalMediaItem;
    MediaItem-->RemoteMediaItem;
    LocalMediaItem-->Image;
    LocalMediaItem-->Video;
    RemoteMediaItem-->Image;
    RemoteMediaItem-->Video;
    AutoAlbum-->Gallery;
    UserAlbum-->Gallery;
    Favourites-->Gallery;
    TrashMedia-->Gallery;
    LocalMedia-->Gallery;
    HiddenMedia-->Gallery;
    
    subgraph Local
        LocalMediaItem;
        LocalMedia;
    end
    subgraph Albums
        AutoAlbum;
        UserAlbum;
    end
    
    subgraph Remote
        RemoteMediaItem;
        Albums;
        Favourites;
        TrashMedia;
        HiddenMedia;
    end
```