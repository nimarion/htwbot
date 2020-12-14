package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class LeaveCommand extends Command {

    public LeaveCommand() {
        super("leave", "Verlässt einen Voicechannel");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
            if (message.getGuild().getAudioManager().getConnectedChannel() != null) {
                embedBuilder.addField(
                        "Verbindung getrennt", "`Channel "
                                + message.getGuild().getAudioManager().getConnectedChannel().getName() + " verlassen`",
                        false);
                message.getGuild().getAudioManager().closeAudioConnection();
            } else {
                embedBuilder.setDescription("Der Bot ist in keinem Channel");
            }
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }
}