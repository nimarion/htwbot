package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class LeaveCommand extends Command {

    public LeaveCommand() {
        super("leave", "Verlässt einen Voicechannel");
    }

    @Override
    public void execute(String[] args, Message message) {
        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        if (message.getGuild().getAudioManager().getConnectedChannel() != null) {
            embedBuilder.addField("Verbindung getrennt", "`Channel " + message.getGuild().getAudioManager().getConnectedChannel().getName() + " verlassen`", false);
        } else {
            embedBuilder.setDescription("Du bist in keinem Voicechannel");
        }

        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
        message.getGuild().getAudioManager().closeAudioConnection();
    }
}