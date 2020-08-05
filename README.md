My fork of [jagrosh's MusicBot](https://github.com/jagrosh/MusicBot).  
**This is not intended for mainstream use**, as it has many features specific to my (very niche) use cases.

The following changes have been made so far:
* Completely removed the ability to play music from YouTube/SoundCloud/etc.
* Rewrote the `play` command to be identical to `play playlist`
* A default playlist now plays when `play` is executed without arguments.
* Added artist name to queue header and entries
* Changed the Unknown messages to funnier ones
* Increased queue length to 15