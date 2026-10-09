# Settings provider

The exported content provider lets a host application inspect the setting schema
and read or change settings without duplicating the settings UI.

- Authority: `com.jeremykenedy.nebuladrift.settings`
- Provider class: `com.jeremykenedy.nebuladrift.NebulaSettingsProvider`
- Schema version: `1`

Use `ContentResolver.call()` with a URI whose authority is the provider above.
The `get_schema` and `get_settings` methods return a `Bundle` containing a JSON
string under the `json` key. `set_setting` takes the setting key as `arg` and a
string `value` in the extras Bundle. Values must match the schema choices; an
integer is clamped to its supported range. The `Random` value enables random
selection for that one field. `set_random_all` and `set_random_none` toggle all
field randomization at once.

The returned schema lists keys, types, defaults, supported values, and numeric
ranges. Unknown methods and setting keys fail with `IllegalArgumentException`.
The provider is local device IPC; it makes no network requests.

Example schema fragment:

```json
{"key":"palette","type":"choice","default":"Violet","random":true,"choices":["Violet","Blue","Emerald","Crimson"]}
```

The settings UI reads the same persisted preferences. DreamService picks up
changes on the next showing.
