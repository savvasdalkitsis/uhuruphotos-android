```toml
name = 'Get Favourites Summary'
description = '{{HOST}}/api/albums/date/:id'
method = 'GET'
url = '{{HOST}}/api/albums/date/:id/?favorite=true'
sortWeight = 2000000
id = 'dc6357d4-f403-4c32-a47e-ebb02675e610'

[[queryParams]]
key = 'favorite'
value = 'true'

[[pathVariables]]
key = 'id'
value = '450'
```
