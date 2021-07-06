package de.nmarion.htwbot.commands;

import de.nmarion.htwbot.HtwBot;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandManager extends ListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

  private final Set<Command> availableCommands;

  public CommandManager(final HtwBot bot) {
    this.availableCommands = new HashSet<>();
    final CommandListUpdateAction commandListUpdateAction = bot.getGuild().updateCommands();
    for (Class<? extends Command> cmdClass :
        new Reflections("de.nmarion.htwbot.commands").getSubTypesOf(Command.class)) {
      try {
        final Command command = cmdClass.getDeclaredConstructor().newInstance();
        command.setInstance(bot);
        command.register(commandListUpdateAction);
        if (availableCommands.add(command)) {
          logger.info("Registered " + command.getCommand() + " Command");
        }
      } catch (Exception exception) {
        logger.error("Error while registering Command!", exception);
      }
    }
    commandListUpdateAction.queue();
    bot.getJDA().addEventListener(this);
  }

  @Override
  public void onSlashCommand(SlashCommandEvent event) {
    if (event.getMember().getRoles().size() == 0) {
      return;
    }
    final Optional<Command> optional =
        availableCommands.stream()
            .filter(command -> command.getCommand().equalsIgnoreCase(event.getName()))
            .findFirst();
    if (optional.isPresent()) {
      optional.get().execute(event);
    } else {
      event.reply("Command existiert nicht").setEphemeral(true).queue();
    }
  }

  public Set<Command> getAvailableCommands() {
    return Collections.unmodifiableSet(availableCommands);
  }
}
