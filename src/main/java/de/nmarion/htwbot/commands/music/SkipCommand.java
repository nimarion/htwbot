package de.nmarion.htwbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class SkipCommand extends Command {

  public SkipCommand() {
    super("skip", "Überspringt einen Track");
  }

  @Override
  public void execute(String[] args, Message message) {
    final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
    if (DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
      getBot().getMusicManager().skip(message.getGuild());
      if (getBot().getMusicManager().getPlayingTrack(message.getGuild()) != null) {
        final AudioTrackInfo trackInfo =
            getBot().getMusicManager().getPlayingTrack(message.getGuild()).getInfo();
        final String length;
        if (TimeUnit.MILLISECONDS.toHours(trackInfo.length) >= 24) {
          length =
              String.format(
                  "%dd %02d:%02d:%02d",
                  TimeUnit.MILLISECONDS.toDays(trackInfo.length),
                  TimeUnit.MILLISECONDS.toHours(trackInfo.length) % 24,
                  TimeUnit.MILLISECONDS.toMinutes(trackInfo.length) % 60,
                  TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60);
        } else {
          length =
              String.format(
                  "%02d:%02d:%02d",
                  TimeUnit.MILLISECONDS.toHours(trackInfo.length) % 24,
                  TimeUnit.MILLISECONDS.toMinutes(trackInfo.length) % 60,
                  TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60);
        }
        embedBuilder.addField(
            trackInfo.title,
            "`" + trackInfo.author + " - " + (trackInfo.isStream ? "STREAM" : length) + "`",
            false);
      } else {
        embedBuilder.setDescription("Es gibt kein weiteres Lied in der Warteschlange");
      }
    }
    message.getTextChannel().sendMessage(embedBuilder.build()).queue();
  }
}
