package de.nmarion.htwbot;

import de.nmarion.htwbot.commands.CommandManager;
import de.nmarion.htwbot.listener.guild.GuildMemberJoinListener;
import de.nmarion.htwbot.listener.guild.GuildMemberLeaveListener;
import de.nmarion.htwbot.listener.guild.GuildMemberNickChangeListener;
import de.nmarion.htwbot.listener.other.GuildReadyListener;
import de.nmarion.htwbot.listener.voice.GuildVoiceListener;
import de.nmarion.htwbot.music.MusicManager;
import de.nmarion.htwbot.tempchannel.Tempchannel;
import io.sentry.Sentry;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtwBot {

  private static final Logger logger = LoggerFactory.getLogger(HtwBot.class);

  private final JDA jda;
  private final MusicManager musicManager;
  private final CommandManager commandManager;
  private final Map<String, Tempchannel> tempchannels;

  public HtwBot() throws Exception {
    final long startTime = System.currentTimeMillis();
    logger.info("Starting htwbot");

    tempchannels = new HashMap<>();

    musicManager = new MusicManager(this);
    jda = initJDA(Configuration.DISCORD_TOKEN);
    logger.info("JDA set up!");

    commandManager = new CommandManager(this);
    logger.info("Command-Manager set up!");

    new GuildVoiceListener(this);
    new GuildMemberJoinListener(this);
    new GuildMemberLeaveListener(this);
    new GuildMemberNickChangeListener(this);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  jda.shutdown();
                }));

    logger.info(String.format("Startup finished in %dms!", System.currentTimeMillis() - startTime));
  }

  private JDA initJDA(final String token) throws Exception {
    JDABuilder builder =
        JDABuilder.createDefault(
            token,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_VOICE_STATES);
    builder.enableCache(CacheFlag.MEMBER_OVERRIDES);
    builder.addEventListeners(new GuildReadyListener(this));
    builder.setMemberCachePolicy(MemberCachePolicy.ALL);

    try {
      return builder.build().awaitReady();
    } catch (LoginException exception) {
      throw exception;
    }
  }

  public JDA getJDA() {
    return jda;
  }

  public MusicManager getMusicManager() {
    return musicManager;
  }

  public CommandManager getCommandManager() {
    return commandManager;
  }

  public Map<String, Tempchannel> getTempchannels() {
    return tempchannels;
  }

  public Guild getGuild() {
    return jda.getGuilds().get(0);
  }

  public static void main(String[] args) {
    if (System.getenv("SENTRY_DSN") != null || System.getProperty("sentry.properties") != null) {
      Sentry.init();
    }
    try {
      new HtwBot();
    } catch (Exception exception) {
      logger.error("Encountered exception while initializing the bot!", exception);
    }
  }
}
