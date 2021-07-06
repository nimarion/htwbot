package de.nmarion.htwbot.commands;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class InfoCommand extends Command {

  public InfoCommand() {
    super("info", "Zeigt Infos über den Bot");
  }

  @Override
  public void register(CommandListUpdateAction commandListUpdateAction) {
    commandListUpdateAction.addCommands(new CommandData(getCommand(), getDescription()));
  }

  @Override
  public void execute(SlashCommandEvent event) {
    final JDA jda = event.getJDA();
    final long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

    final EmbedBuilder embedBuilder = getEmbed(event);
    embedBuilder.addField("Ping", jda.getGatewayPing() + "ms", true);
    embedBuilder.addField("Uptime",
        String.valueOf(TimeUnit.MILLISECONDS.toDays(uptime) + "d " + TimeUnit.MILLISECONDS.toHours(uptime) % 24 + "h "
            + TimeUnit.MILLISECONDS.toMinutes(uptime) % 60 + "m " + TimeUnit.MILLISECONDS.toSeconds(uptime) % 60 + "s"),
        true);
    embedBuilder.addField("Commands", String.valueOf(getBot().getCommandManager().getAvailableCommands().size()), true);
    embedBuilder.addField("Mitglieder", String.valueOf(jda.getGuilds().stream().mapToInt(Guild::getMemberCount).sum()),
        true);
    embedBuilder.addField("Java Version", System.getProperty("java.runtime.version").replace("+", "_"), true);
    embedBuilder.addField("Betriebssystem", ManagementFactory.getOperatingSystemMXBean().getName(), true);
    event.reply(new MessageBuilder().setEmbeds(embedBuilder.build()).build()).setEphemeral(true).queue();
  }

}
