This page demonstrates the structure of each feature module in the app

(still WIP)

```mermaid 
graph TD;

    subgraph Domain
        Usecase
        Service
        Repository
    end

    subgraph View
        Navigation
        Seam
        Ui
        ViewModel
    end

    subgraph Feature
        View
        Domain
    end
```