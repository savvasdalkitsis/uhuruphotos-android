```toml
name = 'Log in'
description = '{{HOST}}/api/auth/token/obtain'
method = 'POST'
url = '{{HOST}}/api/auth/token/obtain/'
sortWeight = 1000000
id = 'aff92345-6f75-44b6-af46-04a8ba63d9c9'

[auth]
type = 'NO_AUTH'

[body]
type = 'JSON'
raw = '''
{
  "username": "{{USERNAME}}",
  "password": "{{PASSWORD}}"
}'''
```
