package de.nmarion.htwbot.listener.guild;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberLeaveListener extends ListenerAdapter {

  public GuildMemberLeaveListener(final HtwBot bot) {
    bot.getJDA().addEventListener(this);
  }

  @Override
  public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
    if (Configuration.BOT_CHANNEL != null) {
      final EmbedBuilder embedBuilder = DiscordUtils.getDefaultEmbed(event.getMember());
      embedBuilder.appendDescription(
          event.getMember().getEffectiveName() + " hat den Server verlassen");
      event
          .getGuild()
          .getTextChannelById(Configuration.BOT_CHANNEL)
          .sendMessageEmbeds(embedBuilder.build())
          .queue();
    }
  }
}
