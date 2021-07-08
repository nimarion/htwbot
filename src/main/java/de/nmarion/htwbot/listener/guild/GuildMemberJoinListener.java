package de.nmarion.htwbot.listener.guild;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.utils.DiscordUtils;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberJoinListener extends ListenerAdapter {

  public GuildMemberJoinListener(final HtwBot bot) {
    bot.getJDA().addEventListener(this);
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    if (Configuration.BOT_CHANNEL != null) {
      final Member member = event.getMember();
      final EmbedBuilder logBuilder = DiscordUtils.getDefaultEmbed(member);
      logBuilder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
      logBuilder.appendDescription(
          "Erstelldatum: "
              + member
                  .getUser()
                  .getTimeCreated()
                  .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
              + "\n");
      logBuilder.appendDescription(
          "Standard Avatar: " + (member.getUser().getAvatarUrl() == null) + "\n");
      member
          .getGuild()
          .getTextChannelById(Configuration.BOT_CHANNEL)
          .sendMessageEmbeds(logBuilder.build())
          .queue();
    }
  }
}
