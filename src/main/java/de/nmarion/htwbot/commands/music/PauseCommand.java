package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class PauseCommand extends Command {

    public PauseCommand() {
        super("pause", "Pausiert einen Track");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (DiscordUtils.isConnected(message.getMember(), embedBuilder)) {
            final boolean paused = getBot().getMusicManager().isPaused(message.getGuild());
            getBot().getMusicManager().setPaused(message.getGuild(), !paused);
            final String pausedString = !paused ? "pausiert" : "fortgesetzt";
            embedBuilder.addField("Track " + pausedString, "`Der aktuelle Track wurde " + pausedString + ".`", false);
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }
}
