package de.nmarion.htwbot.listener.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.utils.DiscordUtils;

public class GuildMemberNickChangeListener extends ListenerAdapter {

    public GuildMemberNickChangeListener(final HtwBot bot) {
        bot.getJDA().addEventListener(this);
    }

    @Override
    public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
        if (Configuration.BOT_CHANNEL != null) {
            final EmbedBuilder logBuilder = DiscordUtils.getDefaultEmbed(event.getMember());
            logBuilder.setTitle("Nickname geändert");
            logBuilder.appendDescription("Neuer Nickname:" + event.getNewNickname() + "\n");
            logBuilder.appendDescription("Alter Nickname: " + event.getOldNickname());
            event.getGuild().getTextChannelById(Configuration.BOT_CHANNEL).sendMessage(logBuilder.build()).queue();
        }
    }

}