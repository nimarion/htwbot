package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PauseCommand extends Command {

    public PauseCommand() {
        super("pause", "Pausiert einen Track");
    }

    @Override
    public void execute(String[] args, Message message) {
        TextChannel channel = message.getTextChannel();
        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (message.getMember().getVoiceState() != null && message.getMember().getVoiceState().inVoiceChannel()) {
            boolean paused = getBot().getMusicManager().isPaused(message.getGuild());
            getBot().getMusicManager().setPaused(message.getGuild(), !paused);
            String pausedString = !paused ? "pausiert" : "fortgesetzt";
            embedBuilder.addField("Track " + pausedString, "`Der aktuelle Track wurde " + pausedString + ".`", false);
        } else {
            embedBuilder.setDescription("Du bist in keinem Voicechannel");
        }
        channel.sendMessage(embedBuilder.build()).queue();
    }
}
