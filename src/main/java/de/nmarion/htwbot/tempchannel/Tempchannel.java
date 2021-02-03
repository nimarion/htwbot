package de.nmarion.htwbot.tempchannel;

import java.util.Arrays;
import java.util.List;

import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Tempchannel implements TempchannelEvents {

  private static final List<Permission> MEMBER_PERMISSIONS;

  static {
    MEMBER_PERMISSIONS = Arrays.asList(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
  }

  private TextChannel textChannel;

  public Tempchannel() {
  }

  public Tempchannel(final TextChannel textChannel) {
    this.textChannel = textChannel;
  }

  @Override
  public void onTempchannelJoin(final VoiceChannel voiceChannel, final Member member) {
    if (voiceChannel.equals(voiceChannel.getGuild().getAfkChannel())) {
      return;
    }
    if (textChannel == null) {
      final Role piRole = member.getGuild().getRolesByName("Praktische Informatik", true).get(0);
      final Role kiRole = member.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0);
      final Role dfhiRole = member.getGuild().getRolesByName("DFHI", true).get(0);
      final Guild guild = voiceChannel.getGuild();
      voiceChannel.getParent().createTextChannel("temp-" + voiceChannel.getName().toLowerCase())
          .addPermissionOverride(guild.getSelfMember(), MEMBER_PERMISSIONS, null)
          .addPermissionOverride(member, MEMBER_PERMISSIONS, null)
          .addPermissionOverride(piRole, null, MEMBER_PERMISSIONS)
          .addPermissionOverride(kiRole, null, MEMBER_PERMISSIONS)
          .addPermissionOverride(dfhiRole, null, MEMBER_PERMISSIONS)
          .addPermissionOverride(guild.getPublicRole(), null, MEMBER_PERMISSIONS).queue(channel -> {
            setTextChannel((TextChannel) channel);
            if (voiceChannel.getName().toLowerCase().contains("custom")) {
              final EmbedBuilder embedBuilder = DiscordUtils.getDefaultEmbed(member);
              embedBuilder
                  .setDescription(member.getAsMention() + " mit !limit kannst du das Maximale Nutzer Limit ändern");
              channel.sendMessage(embedBuilder.build()).queue();
            }
          });
    } else {
      if (textChannel.getPermissionOverride(member) != null) {
        textChannel.getPermissionOverride(member).getManager().grant(MEMBER_PERMISSIONS).queue();
      } else {
        textChannel.createPermissionOverride(member).setAllow(MEMBER_PERMISSIONS).queue();
      }
      if (member.getUser().isBot()) {
        return;
      }
      final EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setColor(member.getColor());
      embedBuilder.setDescription(":arrow_right: " + member.getAsMention() + " ist beigetreten");
      textChannel.sendMessage(embedBuilder.build()).queue();
    }
  }

  @Override
  public void onTempchannelLeave(final VoiceChannel voiceChannel, final Member member) {
    if (voiceChannel.equals(voiceChannel.getGuild().getAfkChannel())) {
      return;
    }
    if (textChannel == null) {
      return;
    }
    if (voiceChannel.getMembers().isEmpty()) {
      textChannel.delete().queue();
      textChannel = null;
    } else if (textChannel.getPermissionOverride(member) != null) {
      textChannel.getPermissionOverride(member).delete().queue();
      if (member.getUser().isBot()) {
        return;
      }
      final EmbedBuilder embedBuilder = new EmbedBuilder();
      embedBuilder.setColor(member.getColor());
      embedBuilder.setDescription(":arrow_left: " + member.getAsMention() + " hat uns verlassen");
      textChannel.sendMessage(embedBuilder.build()).queue();
    }
  }

  @Override
  public void onLoad(final TextChannel textChannel, final VoiceChannel voiceChannel) {
    if (voiceChannel.getMembers().isEmpty()) {
      textChannel.delete().queue();
      setTextChannel(null);
      return;
    }

    for (Member member : textChannel.getMembers()) {
      if (member.hasPermission(Permission.MESSAGE_MANAGE)) {
        continue;
      }
      if (voiceChannel.getMembers().contains(member)) {
        if (textChannel.getPermissionOverride(member) != null) {
          textChannel.getPermissionOverride(member).getManager()
              .grant(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
        } else {
          textChannel.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
              .queue();
        }
      } else {
        textChannel.getPermissionOverride(member).delete().queue();
      }
    }
  }

  private void setTextChannel(final TextChannel textChannel) {
    this.textChannel = textChannel;
  }
}
