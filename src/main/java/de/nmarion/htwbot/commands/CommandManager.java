package de.nmarion.htwbot.commands;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.HtwBot;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandManager extends ListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
  private static final Pattern MENTION_PATTERN = MentionType.USER.getPattern();

  private final Set<Command> availableCommands;

  public CommandManager(final HtwBot bot) {
    this.availableCommands = new HashSet<>();
    final Set<Class<? extends Command>> classes =
        new Reflections("de.nmarion.htwbot.commands").getSubTypesOf(Command.class);
    for (Class<? extends Command> cmdClass : classes) {
      try {
        final Command command = cmdClass.getDeclaredConstructor().newInstance();
        command.setInstance(bot);
        if (availableCommands.add(command)) {
          logger.info("Registered " + command.getCommand() + " Command");
        }
      } catch (Exception exception) {
        logger.error("Error while registering Command!", exception);
      }
    }
    bot.getJDA().addEventListener(this);
  }

  @Override
  public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot() || event.getMember().getRoles().size() == 0) {
      return;
    }
    final String content = event.getMessage().getContentRaw();
    final PrefixType prefixType = checkPrefix(content, event.getGuild().getSelfMember().getId());
    if (prefixType != PrefixType.NONE) {
      final Optional<Command> optional =
          availableCommands.stream()
              .filter(
                  command -> command.getCommand().equalsIgnoreCase(getCommand(content, prefixType)))
              .findFirst();
      if (optional.isPresent()) {
        optional.get().execute(getArguments(content, prefixType), event.getMessage());
      }
    }
  }

  private PrefixType checkPrefix(final String content, final String botId) {
    if (content.split(" ").length > 1) {
      final Matcher matcher = MENTION_PATTERN.matcher(content.split(" ")[0]);
      if (matcher.matches() && matcher.group(1).equals(botId)) {
        return PrefixType.MENTION;
      }
    }
    if (Configuration.DISCORD_PREFIX != null && content.startsWith(Configuration.DISCORD_PREFIX)) {
      return PrefixType.PREFIX;
    }
    return PrefixType.NONE;
  }

  private String[] getArguments(final String content, final PrefixType prefixType) {
    final String[] arguments = content.split(" ");
    return Arrays.copyOfRange(arguments, prefixType == PrefixType.PREFIX ? 1 : 2, arguments.length);
  }

  private String getCommand(final String content, final PrefixType prefixType) {
    final String[] arguments = content.split(" ");
    if (prefixType == PrefixType.PREFIX) {
      return arguments[0].replaceFirst("\\" + Configuration.DISCORD_PREFIX, "");
    }
    if (arguments.length >= 2) {
      return arguments[1];
    }
    return null;
  }

  public Set<Command> getAvailableCommands() {
    return Collections.unmodifiableSet(availableCommands);
  }

  private enum PrefixType {
    MENTION,
    PREFIX,
    NONE
  }
}
