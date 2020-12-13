package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class ClearQueueCommand extends Command {

    public ClearQueueCommand() {
        super("clearqueue", "Leert die Warteschlange");
    }

    @Override
    public void execute(String[] args, Message message) {
        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (message.getMember().getVoiceState() != null && message.getMember().getVoiceState().inVoiceChannel()) {
            getBot().getMusicManager().clearQueue(message.getGuild());
            getBot().getMusicManager().stop(message.getGuild());
            embedBuilder.setDescription("Warteschlange geleert");
        } else {
            embedBuilder.setDescription("Du bist in keinem Voicechannel ^^");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }
}