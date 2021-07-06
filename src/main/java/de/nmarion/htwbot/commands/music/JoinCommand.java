package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class JoinCommand extends Command {

  public JoinCommand() {
    super("join", "Betritt einen Voicechannel");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    final AudioManager audioManager = event.getGuild().getAudioManager();
    if (DiscordUtils.isConnected(event)) {
      if (audioManager.isConnected()) {
        saySilent(event, "Der Bot ist bereits verbunden");
      } else if (!event.getMember().getVoiceState().inVoiceChannel()) {
        saySilent(event, "Dafür musst du in einem Voicechannel sein.");
      } else {
        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        event.getGuild().getAudioManager().openAudioConnection(channel);
        saySilent(event, "Channel " + channel.getName() + " betreten");
      }
    }
  }
}
