package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class RepeatCommand extends Command {

  public RepeatCommand() {
    super("repeat", "Widerholt die Musik");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      if (event.getGuild().getAudioManager().getConnectedChannel() == null) {
        event
            .getGuild()
            .getAudioManager()
            .openAudioConnection(event.getMember().getVoiceState().getChannel());
      }
      getBot()
          .getMusicManager()
          .setRepeat(event.getGuild(), !getBot().getMusicManager().isRepeat(event.getGuild()));
      say(
          event,
          getBot().getMusicManager().isRepeat(event.getGuild())
              ? "Musik wird jetzt wiederholt :repeat:"
              : "Musik spielt jetzt wieder normal");
    }
  }
}
