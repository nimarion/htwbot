package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class ClearQueueCommand extends Command {

  public ClearQueueCommand() {
    super("clearqueue", "Leert die Warteschlange");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      getBot().getMusicManager().clearQueue(event.getGuild());
      getBot().getMusicManager().stop(event.getGuild());
      say(event, "Warteschlange geleert");
    }
  }
}
