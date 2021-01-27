package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class RepeatCommand extends Command {

  public RepeatCommand() {
    super("repeat", "Widerholt die Musik");
  }

  @Override
  public void execute(String[] args, Message message) {
    final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
    if (DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
      if (message.getGuild().getAudioManager().getConnectedChannel() == null) {
        message
            .getGuild()
            .getAudioManager()
            .openAudioConnection(message.getMember().getVoiceState().getChannel());
      }
      getBot()
          .getMusicManager()
          .setRepeat(message.getGuild(), !getBot().getMusicManager().isRepeat(message.getGuild()));
      embedBuilder.setDescription(
          getBot().getMusicManager().isRepeat(message.getGuild())
              ? "Musik wird jetzt wiederholt :repeat:"
              : "Musik spielt jetzt wieder normal");
    }
    message.getTextChannel().sendMessage(embedBuilder.build()).queue();
  }
}
