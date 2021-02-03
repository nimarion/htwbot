package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends Command {

  public JoinCommand() {
    super("join", "Betritt einen Voicechannel");
  }

  @Override
  public void execute(String[] args, Message message) {
    final AudioManager audioManager = message.getGuild().getAudioManager();
    final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
    if (DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
      if (audioManager.isConnected()) {
        embedBuilder.setDescription("Der Bot ist bereits verbunden");
      } else if (!message.getMember().getVoiceState().inVoiceChannel()) {
        embedBuilder.setDescription("Dafür musst du in einem Voicechannel sein.");
      } else {
        VoiceChannel channel = message.getMember().getVoiceState().getChannel();
        message.getGuild().getAudioManager().openAudioConnection(channel);
        embedBuilder.setDescription("Channel " + channel.getName() + " betreten");
      }
    }
    message.getTextChannel().sendMessage(embedBuilder.build()).queue();
  }
}
