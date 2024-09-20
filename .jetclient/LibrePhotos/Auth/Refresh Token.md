```toml
name = 'Refresh Token'
description = '{{HOST}}/api/auth/token/refresh'
method = 'POST'
url = '{{HOST}}/api/auth/token/refresh'
sortWeight = 2000000
id = 'b26813b9-a307-4cc6-bcdf-65c43ed4574a'

[body]
type = 'JSON'
raw = '''
{
  "refresh": "{{REFRESH}}"
}'''
```
