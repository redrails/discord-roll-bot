# Numberwang

# Get started

1. Edit the `src/main/resources/app.config.sample` to include your own Discord bot token, see [this](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token).
2. Build the project: `gradle build`
3. Generate a jar file: `gradle shadowJar`
4. Run the bot: `java -DBOT_TOKEN=your_token -jar ./build/libs/numberwang.jar` or set the `BOT_TOKEN` environment variable in your shell before running the jar.

# Current logic
Generates a random number using the `java.util.Random` class for each candidate.