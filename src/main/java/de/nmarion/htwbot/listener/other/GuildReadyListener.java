package de.nmarion.htwbot.listener.other;

import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.tempchannel.Tempchannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildReadyListener extends ListenerAdapter {

  private final HtwBot bot;

  public GuildReadyListener(final HtwBot bot) {
    this.bot = bot;
  }

  @Override
  public void onGuildReady(GuildReadyEvent event) {
    initTempchannel(event.getGuild());
  }

  private void initTempchannel(final Guild guild) {
    for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
      final String name = ("temp-" + voiceChannel.getName().toLowerCase()).replaceAll("\\s+", "-");
      final TextChannel textChannel =
          voiceChannel.getGuild().getTextChannelsByName(name, true).isEmpty()
              ? null
              : voiceChannel.getGuild().getTextChannelsByName(name, true).get(0);
      if (textChannel == null) {
        bot.getTempchannels().put(voiceChannel.getId(), new Tempchannel());
      } else {
        Tempchannel tempchannel = new Tempchannel(textChannel);
        tempchannel.onLoad(textChannel, voiceChannel);
        bot.getTempchannels().put(voiceChannel.getId(), tempchannel);
      }
    }
  }
}
