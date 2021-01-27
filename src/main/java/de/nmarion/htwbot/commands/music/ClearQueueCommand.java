package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class ClearQueueCommand extends Command {

  public ClearQueueCommand() {
    super("clearqueue", "Leert die Warteschlange");
  }

  @Override
  public void execute(String[] args, Message message) {
    final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
    if (DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
      getBot().getMusicManager().clearQueue(message.getGuild());
      getBot().getMusicManager().stop(message.getGuild());
      embedBuilder.setDescription("Warteschlange geleert");
    }
    message.getTextChannel().sendMessage(embedBuilder.build()).queue();
  }
}
