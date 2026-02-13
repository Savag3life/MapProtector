# Protect Map Art
Map Protector allows players to sign a given filled map with their name, so it cannot be copied by other players. Keeping rare map arts truely rare.

- Protected from anvil actions & renaming
- Cartography table patched
- No crafting or expanding

## Config

```yaml
item:
  # Add the "glow" effect to the item
  enchanted: true
  # Change the map display name when signed
  # Leave empty to use the default
  display_name: ""
  # Lore appended to the item when signed
  signature:
    - "<gray>ꜱɪɢɴᴇᴅ ʙʏ %name%"

messages:
  # Generic no permission
  no_permission: "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold> <red>You do not have permission to do that."
  # Response from using /sign
  signed: "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold> <green>Map successfully signed! No one can copy this map now."
  # Response when /signinfo on a map which isn't signed.
  not_signed: "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold><white> This map isn't protected by a signature."
  not_map: "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold> <white>Only filled maps can be signed."
  sign_info:
    - "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold> <white>%name% protected this map %date% (%ago%)"
  already_signed: "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold> <white>This map has already been signed. It's protected from further changes."
  cannot_copy: "<yellow><bold>ᴄᴏᴘʏʀɪɢʜᴛ <gray>»</bold> <white>This map is copy protected! Use <click:run_command:/signature-info><underlined>/signature-info</underlined> to see the original creator."
```
