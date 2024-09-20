```toml
name = 'Add photo to album'
description = '{{HOST}}/api/albums/user/edit/:id'
method = 'PATCH'
url = '{{HOST}}/api/albums/user/edit/:id/'
sortWeight = 4000000
id = '4abea1bf-b9fc-4109-be58-e49d64d1d4ec'

[[pathVariables]]
key = 'id'
value = '4'

[body]
type = 'JSON'
raw = '{"title":"Test","photos":["8a99ff6a6b0cb1bd147fd14e7d179dc91"]}'
```
