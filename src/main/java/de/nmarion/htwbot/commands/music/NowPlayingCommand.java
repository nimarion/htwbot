package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class NowPlayingCommand extends Command {

    public NowPlayingCommand() {
        super("nowplaying", "Zeigt den aktuell gespielten Track an");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (message.getGuild().getAudioManager().getConnectedChannel() != null
                && getBot().getMusicManager().getPlayingTrack(message.getGuild()) != null) {
            embedBuilder.setDescription("Es wird gerade **"
                    + getBot().getMusicManager().getPlayingTrack(message.getGuild()).getInfo().title + "** gespielt");
        } else {
            embedBuilder.setDescription("Aktuell wird nichts gespielt");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

}
