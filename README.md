# Fireworky
A GUI based firework editor

## Commands:
The base command is ``/fireworky|fwy``, it requires the player to have the ``fireworky.command`` to use. By default with no arguments, opens the firework editor.


### Info
``/fireworky info`` displays a bit of information about the current version

### Launch
``/fireworky launch|l <firework id>`` Launches the firework with given id at the sender's location

### Launch at location
``/fireworky launchatlocation|latl <firework id> <world> <x> <y> <z>`` Launches a firework with given id at the specified location

### Add
``/fireworky add <firework id>`` Creates a firework with a specified id, useful for organizing.

### Reload
``/fireworky reload`` Reloads the config values

---

To rename a firework, go to the Fireworky folder inside of your plugins folder and rename the associated json file of the firework. Note, only edit the name when the server is not running or else it will be overwritten.

By default, fireworks are saved every 15 changes, and when the server shuts down. This value is modifiable in the config.

For any issues or if in need of support, join [The Bakery](https://discord.gg/mDhaSSEV3m)
