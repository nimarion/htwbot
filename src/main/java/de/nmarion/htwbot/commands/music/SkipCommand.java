package de.nmarion.htwbot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SkipCommand extends Command {

  public SkipCommand() {
    super("skip", "Überspringt einen Track");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      getBot().getMusicManager().skip(event.getGuild());
      if (getBot().getMusicManager().getPlayingTrack(event.getGuild()) != null) {
        final AudioTrackInfo trackInfo =
            getBot().getMusicManager().getPlayingTrack(event.getGuild()).getInfo();
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
        final EmbedBuilder embedBuilder =
            getEmbed(event)
                .addField(
                    trackInfo.title,
                    "`" + trackInfo.author + " - " + (trackInfo.isStream ? "STREAM" : length) + "`",
                    false);
        event.reply(new MessageBuilder().setEmbeds(embedBuilder.build()).build()).queue();
      } else {
        saySilent(event, "Es gibt kein weiteres Lied in der Warteschlange");
      }
    }
  }
}
