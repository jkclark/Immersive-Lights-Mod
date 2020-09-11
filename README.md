# JKClark's Immersive Lights Mod

This mod works with an [IFTTT](https://ifttt.com/) service to change your lights to a specific color based on your current biome in Minecraft. Currently works with Minecraft/Forge 1.14.

Of course, you need to have IFTTT-compatible light bulbs to make anything happen. I use Smart Life light bulbs, which integrate with IFTTT.

The IFTTT applet I made uses filter code, so I can't publish it for free. If there is enough demand, I'll happily pay to publish it.

You can create your own IFTTT applet through [IFTTT Platform](https://platform.ifttt.com) (for free) to make the mod work with your lights. Here are the steps:

1. Create an an applet on IFTTT Platform.
2. Choose "Webhooks" as your trigger.
    - For "Field label" put "Event Name" (I'm not actually sure if this matters).
    - For "Default value" put `minecraft_biome_changed`.
3. Choose the service that corresponds to your light bulb.
    - Fill in the fields in a way that makes sense to you.
4. Paste the filter code from `filter_code.js` into the Filter Code section. You will have to make some adjustments if you don't use Smart Life bulbs.
    - You can set the light color for any biome category by changing the "#XXXXXX" in the corresponding `case`.
    - As of right now, you can't specify colors down to the exact biome -- maybe I'll add that capability in a future version.
5. Connect your IFTTT account to the applet you just made, and fill in any required fields.
5. In `ImmersiveLightsMod.java`, set the value of `iftttKey` to your IFTTT API key. You can find this key by going to the [IFTTT Webhooks](https://ifttt.com/maker_webhooks) page and clicking "Documentation" in the top right.

And you're done! I hope someone out there gets something out of this. It was really fun to make (although frustrating at times).

Please feel free to contact me, create issues and pull requests, etc.

Happy immersing!

### Changelog:
- 09/21/2019:
    - Lights now change brightness based on in-game time of day.
- 09/10/2019:
    - First commit. Lights change color based on biome categories.
