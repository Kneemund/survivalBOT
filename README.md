# survivalBOT
A spigot plugin for Minecraft servers that allows managing a custom, Minecraft UUID and Discord User ID based whitelisting system via Discord.

## Features
- In order to get whitelisted, members of the Discord server have to use the `!whitelist <mc_username>` command in a specified channel. Administrators can then react to the whitelist request with ✅ and ❌ to accept or deny it.
- Administrators can manage the whitelist using commands like `!add <mc_username> <userID|@user>`, `!remove <mc_username> <userID|@user>` and `!removeAll <userID|@user>`.
- Discord users can have multiple Minecraft accounts linked to them, but one Minecraft account can not be linked to multiple Discord users.
- Utility commands like `!getUsername <uuid>`, `!getUUID <uuid>` and `!info <userID|@user>`.
- If a user leaves the Discord server, their linked Minecraft account(s) automatically get(s) removed from the whitelist.
- If a user gets removed from the whitelist while playing on the Minecraft server, they get kicked.

Use `!help` to get a full list of commands with explanations.
