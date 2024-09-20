```toml
name = 'Move to trash'
description = '{{HOST}}/api/photosedit/setdeleted'
method = 'POST'
url = '{{HOST}}/api/photosedit/setdeleted'
sortWeight = 1000000
id = 'cae69cf4-0f9c-48cd-8722-63cff5cc9dce'

[body]
type = 'JSON'
raw = '''
{
    "deleted": true,
    "image_hashes": ["5858ae5d2a38505d5c083fb6ffa0c0161"]
}'''
```
