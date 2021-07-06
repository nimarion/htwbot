package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import de.nmarion.htwbot.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class PlayCommand extends Command {

  public PlayCommand() {
    super("play", "Spielt etwas Musik");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription())
        .addOptions(new OptionData(OptionType.STRING, "song", "Youtube,Twitch,Soundcloud,...").setRequired(true)));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      event.deferReply().queue();
      if (event.getGuild().getAudioManager().getConnectedChannel() == null) {
        event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
      }
      final String song = event.getOption("song").getAsString();
      getBot().getMusicManager().setPaused(event.getGuild(), false);
      if (StringUtils.extractUrls(song).isEmpty()) {
        getBot().getMusicManager().loadTrack(event, "ytsearch:" + String.join(" ", song));
      } else {
        getBot().getMusicManager().loadTrack(event, song);
      }
    }
  }

}
