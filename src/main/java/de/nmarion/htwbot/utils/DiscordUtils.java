package de.nmarion.htwbot.utils;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class DiscordUtils {

  public static EmbedBuilder getDefaultEmbed(final User user) {
    return new EmbedBuilder()
        .setFooter(
            "@" + user.getName() + "#" + user.getDiscriminator(), user.getEffectiveAvatarUrl())
        .setColor(Color.GREEN);
  }

  public static EmbedBuilder getDefaultEmbed(final Member member) {
    return getDefaultEmbed(member.getUser()).setColor(member.getGuild().getSelfMember().getColor());
  }

  public static boolean isConnected(final Member member, final EmbedBuilder embedBuilder) {
    if (member.getVoiceState() != null && member.getVoiceState().inVoiceChannel()) {
      return true;
    }
    embedBuilder.setDescription("Du bist in keinem Voicechannel");
    return false;
  }

  public static boolean isConnected(SlashCommandEvent event) {
    if (event.getMember().getVoiceState() != null
        && event.getMember().getVoiceState().inVoiceChannel()) {
      return true;
    }
    event.reply("Du bist in keinem Voicechannel").setEphemeral(true).queue();
    return false;
  }
}
