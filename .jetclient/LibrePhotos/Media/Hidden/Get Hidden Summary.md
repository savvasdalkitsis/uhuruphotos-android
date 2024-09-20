```toml
name = 'Get Hidden Summary'
description = '{{HOST}}/api/albums/date/:id'
method = 'GET'
url = '{{HOST}}/api/albums/date/:id/?hidden=true'
sortWeight = 2000000
id = 'cbe63741-3033-4505-8f4d-eff8a6960801'

[[queryParams]]
key = 'hidden'
value = 'true'

[[queryParams]]
value = ''
disabled = true

[[pathVariables]]
key = 'id'
value = '1480'
```
