```toml
name = 'Get Trash Summary'
description = '{{HOST}}/api/albums/date/:id'
method = 'GET'
url = '{{HOST}}/api/albums/date/:id/?in_trashcan=true'
sortWeight = 2000000
id = '1bc6885a-4d3c-4497-b17c-da602c82c3ea'

[[queryParams]]
value = ''
disabled = true

[[queryParams]]
key = 'in_trashcan'
value = 'true'

[[pathVariables]]
key = 'id'
value = '1839'
```
