package de.nmarion.htwbot.utils;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class DiscordUtils {

    public static EmbedBuilder getDefaultEmbed(final User user) {
        return new EmbedBuilder()
                .setFooter("@" + user.getName() + "#" + user.getDiscriminator(), user.getEffectiveAvatarUrl())
                .setColor(Color.GREEN);
    }

    public static EmbedBuilder getDefaultEmbed(final Member member) {
        return getDefaultEmbed(member.getUser()).setColor(member.getGuild().getSelfMember().getColor());
    }
    
}
