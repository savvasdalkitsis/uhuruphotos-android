This page demonstrates the domain model of the app

(still WIP)

```mermaid
graph TD;
    Gallery-->Feed;
    Feed-->FeedGroup;
    FeedGroup-->MediaItem;
    MediaItem-->LocalMediaItem;
    MediaItem-->RemoteMediaItem;
    AutoAlbum-->Gallery;
    UserAlbum-->Gallery;
    Favourites-->Gallery;
    Trash-->Gallery;
    LocalFolder-->Gallery;
```