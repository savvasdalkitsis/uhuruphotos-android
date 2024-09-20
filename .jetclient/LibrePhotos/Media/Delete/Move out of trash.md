```toml
name = 'Move out of trash'
description = '{{HOST}}/api/photosedit/setdeleted'
method = 'POST'
url = '{{HOST}}/api/photosedit/setdeleted'
sortWeight = 2000000
id = '7fc673f7-4966-4c63-9542-81a43e9d31d8'

[body]
type = 'JSON'
raw = '''
{
    "deleted": false,
    "image_hashes": ["5858ae5d2a38505d5c083fb6ffa0c0161"]
}'''
```
