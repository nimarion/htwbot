package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class NowPlayingCommand extends Command {

  public NowPlayingCommand() {
    super("nowplaying", "Zeigt den aktuell gespielten Track an");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (event.getGuild().getAudioManager().getConnectedChannel() != null
        && getBot().getMusicManager().getPlayingTrack(event.getGuild()) != null) {
      saySilent(
          event,
          "Es wird gerade **"
              + getBot().getMusicManager().getPlayingTrack(event.getGuild()).getInfo().title
              + "** gespielt");
    } else {
      saySilent(event, "Aktuell wird nichts gespielt");
    }
  }
}
