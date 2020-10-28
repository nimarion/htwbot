package de.nmarion.htwbot.listener.message;

import java.util.concurrent.TimeUnit;

import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.commands.VerifyCommand.VerifyPerson;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceiveListener extends ListenerAdapter {

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
        if (event.getChannel().getName().equals("verifizieren")) {
            try {
                final Integer code = Integer.valueOf(event.getMessage().getContentRaw());
                final Member member = event.getMember();
                final Role piRole = bot.getGuild().getRolesByName("Praktische Informatik", true).get(0);
                final Role kiRole = bot.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0);
                if (!member.getRoles().contains(piRole) && !member.getRoles().contains(kiRole)) {
                    if (bot.getVerifyCodes().containsKey(member)) {
                        final VerifyPerson verifyPerson = bot.getVerifyCodes().get(member);
                        if (code.equals(verifyPerson.getRandomCode())) {
                            member.getGuild()
                                    .addRoleToMember(member, verifyPerson.getMail().startsWith("ki") ? kiRole : piRole)
                                    .queue(success -> bot.getVerifyCodes().remove(member));
                        } else {
                            event.getChannel().sendMessage("Bitte überprüfe deinen Code!").queue(success -> {
                                success.delete().queueAfter(10, TimeUnit.SECONDS);
                            });
                        }
                    }
                }
            } catch (NumberFormatException exception) {
                return;
            } finally {
                event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
            }
        }
    }

}
