package de.nmarion.htwbot.listener.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class MessageReceiveListener extends ListenerAdapter {

    private static final Pattern MENTION_PATTERN = MentionType.USER.getPattern();

    private final HtwBot bot;

    public MessageReceiveListener(final HtwBot bot) {
        this.bot = bot;
        bot.getJDA().addEventListener(this);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        final String content = event.getMessage().getContentRaw();
        if (content.split(" ").length > 1) {
            final Matcher matcher = MENTION_PATTERN.matcher(content.split(" ")[0]);
            if (matcher.matches() && matcher.group(1).equals(bot.getJDA().getSelfUser().getId())) {
                final String[] arguments = content.split(" ");
                switch (arguments[1].toLowerCase()) {
                    case "pib", "pi", "praktische", "praktisch" -> addRole(event.getChannel(), event.getMember(),
                            event.getGuild().getRolesByName("Praktische Informatik", true).get(0));
                    case "kib", "ki", "kommunikation", "kommunikations" -> addRole(event.getChannel(), event.getMember(),
                            event.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0));
                }
            }
        }
    }

    private void addRole(final TextChannel textChannel, Member member, final Role role) {
        if (PermissionUtil.canInteract(member, role)) {
            final EmbedBuilder embedBuilder = DiscordUtils.getDefaultEmbed(member);
            if (member.getRoles().contains(role)) {
                member.getGuild().removeRoleFromMember(member, role).queue(success -> {
                    embedBuilder.appendDescription("Du hast die Rolle " + role.getName() + " verlassen");
                    textChannel.sendMessage(embedBuilder.build()).queue();

                });
            } else {
                member.getGuild().addRoleToMember(member, role).queue(success -> {
                    embedBuilder.appendDescription("Du hast jetzt die Rolle " + role.getName());
                    textChannel.sendMessage(embedBuilder.build()).queue();
                });
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        event.getChannel().sendMessage(":wave:").queue();
    }

}
