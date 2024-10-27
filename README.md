# ⛏️ Deathban Mod

**Deathban** is a Fabric mod for Minecraft that introduces a challenging mechanic to the game: when a player dies, they are temporarily banned from the server for a specified duration. This adds an extra layer of excitement and challenge to survival gameplay.

## 📜 Features

- **Death Ban**: When a player dies, they are banned for a configurable amount of time.
- **Configurable Ban Time**: Easily adjust the default ban duration using a configuration file.
- **Admin Commands**: Manage bans directly with admin commands:
- **Offline Player Support**: Works with both online and offline players.
- **Regular Broadcasts**: Server announcements every 10 minutes.

## 🛠️ Installation

1. **Download the Mod**:
   - Download the latest version from the [Releases](https://github.com/AhmedPatelDev/deathban-fabric-mod/releases) section.

2. **Install Fabric**:
   - Make sure you have the [Fabric Mod Loader](https://fabricmc.net/use/) installed.

3. **Place the Mod**:
   - Put the downloaded `.jar` file into your server's `mods` folder.

4. **Configuration**:
   - After running the server with the mod for the first time, a configuration file (`deathban_config.json`) will be generated in the `config` folder.
   - Adjust the `defaultBanSeconds` value as needed.

## 🚀 Usage

### Commands

- **Ban a Player**: `/deathban ban {username} {hours}`
- **Unban a Player**: `/deathban unban {username}`

### Configuration File

The configuration file is generated in the `config` folder:
```json
{
    "defaultBanSeconds": 129600
}
```

## 📦 Building the Mod

1. **Clone the repository**:
```bash
git clone https://github.com/your-username/deathban.git
cd deathban
```

2. **Build the mod using Gradle**:
```bash
./gradlew build
```

3. **The .jar file will be located in the build/libs directory.**

## 📚 Contributing

Contributions are welcome! If you have suggestions or improvements, feel free to open an issue or create a pull request.

##### Local Setup

Fork and clone the repository.
Import it into your favorite Java IDE (e.g., IntelliJ IDEA).
Run ./gradlew genSources to set up the development environment.

## 📝 License

This project is licensed under the MIT License.

## 📢 Support

If you encounter any issues or have questions, please open an issue on GitHub.

## ❤️ Acknowledgments

Thanks to the Minecraft and Fabric communities for inspiration and resources.
Special shoutout to Fabric API for making modding easier!
