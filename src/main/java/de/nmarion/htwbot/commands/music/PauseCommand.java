package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class PauseCommand extends Command {

  public PauseCommand() {
    super("pause", "Pausiert einen Track");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      final boolean paused = getBot().getMusicManager().isPaused(event.getGuild());
      getBot().getMusicManager().setPaused(event.getGuild(), !paused);
      final String pausedString = !paused ? "pausiert" : "fortgesetzt";
      say(event, "Der aktuelle Track wurde " + pausedString + ".");
    }
  }

}
