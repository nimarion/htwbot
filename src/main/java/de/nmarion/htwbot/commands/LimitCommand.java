package de.nmarion.htwbot.commands;

import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class LimitCommand extends Command {

  public LimitCommand() {
    super("limit", "Limitiert die Anzahl der maximalen Mitglieder in einem Sprachkanal");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(
        new CommandData(getCommand(), getDescription())
            .addOptions(new OptionData(OptionType.INTEGER, "limit", "2-99").setRequired(true)));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    if (DiscordUtils.isConnected(event)) {
      final EmbedBuilder embedBuilder = getEmbed(event);
      final VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
      if (voiceChannel.getName().toLowerCase().contains("custom")) {
        int max = (int) event.getOption("limit").getAsLong();
        if (max >= 2 && max <= 99) {
          voiceChannel.getManager().setUserLimit(max).queue();
          embedBuilder.setDescription("Das Limit ist jetzt " + max);
        } else {
          embedBuilder.setDescription("Das Limit muss zwischen 2 und 99 liegen");
        }
      } else {
        embedBuilder.setDescription("Der Befehl funktioniert nur in **Custom** Sprachkanälen");
      }
      event.reply(new MessageBuilder().setEmbeds(embedBuilder.build()).build()).queue();
    }
  }
}
