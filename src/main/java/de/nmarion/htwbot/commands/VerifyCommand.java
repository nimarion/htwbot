package de.nmarion.htwbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public class VerifyCommand extends Command {

    public VerifyCommand() {
        super("verify", "Startet die Verifizierung von deinem Discord Account");
    }

    @Override
    public void execute(String[] args, Message message) {
        final Role piRole = message.getGuild().getRolesByName("Praktische Informatik", true).get(0);
        final Role kiRole = message.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0);
        final EmbedBuilder embedBuilder = getEmbed(message.getMember());
        if (message.getMember().getRoles().contains(piRole) || message.getMember().getRoles().contains(kiRole)) {
            embedBuilder.setDescription("Du bist bereits verifiziert :wink:");
        } else {
            embedBuilder.setDescription("WIP :construction_worker:");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

}
