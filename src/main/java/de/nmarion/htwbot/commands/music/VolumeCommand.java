package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class VolumeCommand extends Command {

  public VolumeCommand() {
    super("volume", "Ändert die Lautstärke");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(
        new CommandData(getCommand(), getDescription())
            .addOptions(new OptionData(OptionType.INTEGER, "volume", "0-100").setRequired(true)));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      final Guild guild = event.getGuild();
      final EmbedBuilder embedBuilder = getEmbed(event);
      final int volume = (int) event.getOption("volume").getAsLong();
      if (volume < 0 || volume > 100) {
        embedBuilder.addField(
            "Ungültige Lautstärke", "Wert kann nur zwischen 0 und 100 gesetzt werden.", false);
      } else {
        getBot().getMusicManager().setVolume(guild, volume);
        embedBuilder.addField(
            "Neue Lautstärke: " + getBot().getMusicManager().getVolume(guild),
            getVolume(getBot().getMusicManager().getVolume(guild)),
            false);
      }
      event.reply(new MessageBuilder().setEmbeds(embedBuilder.build()).build()).queue();
    }
  }

  private String getVolume(int volume) {
    String s = "";
    for (int i = 10; i > 0; i--) {
      if (i > (volume / 10)) {
        s += ":black_large_square:";
      } else {
        s += ":white_large_square:";
      }
    }
    return s;
  }
}
