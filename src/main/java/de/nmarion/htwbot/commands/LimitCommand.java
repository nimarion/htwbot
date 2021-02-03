package de.nmarion.htwbot.commands;

import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class LimitCommand extends Command {

  public LimitCommand() {
    super("limit", "Limitiert die Anzahl der maximalen Mitglieder in einem Sprachkanal");
  }

  @Override
  public void execute(String[] args, Message message) {
    final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
    if(args.length == 0){
      embedBuilder.setDescription("!limit <zahl>");
    } else if(DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
      final VoiceChannel voiceChannel = message.getMember().getVoiceState().getChannel();
      if(voiceChannel.getName().toLowerCase().contains("custom")){
        try {
          int max = Integer.valueOf(args[0]);
          if(max >= 2){
            voiceChannel.getManager().setUserLimit(max).queue(success -> embedBuilder.setDescription("Das Limit ist jetzt " + max));
          } else{
            embedBuilder.setDescription("Das Limit muss größer als 1 sein");
          }
        } catch (NumberFormatException exception){
          embedBuilder.setDescription("!limit <zahl>");
        }
      } else {
        embedBuilder.setDescription("Der Befehl funktioniert nur in **Custom** Sprachkanälen");
      }
    }
    message.getTextChannel().sendMessage(embedBuilder.build()).queue();
  }
}
