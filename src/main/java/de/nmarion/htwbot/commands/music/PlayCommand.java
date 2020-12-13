package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import de.nmarion.htwbot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class PlayCommand extends Command {

    public PlayCommand() {
        super("play", "Spiele etwas Musik");
    }

    @Override
    public void execute(String[] args, Message message) {
        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        if (message.getMember().getVoiceState() != null && message.getMember().getVoiceState().inVoiceChannel()) {
            if (message.getGuild().getAudioManager().getConnectedChannel() == null) {
                message.getGuild().getAudioManager()
                        .openAudioConnection(message.getMember().getVoiceState().getChannel());
            }
            if (args.length < 1) {
                embedBuilder.setDescription("!play <URL|Titel");
                message.getTextChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                getBot().getMusicManager().setPaused(message.getGuild(), false);
                if (StringUtils.extractUrls(args[0]).isEmpty()) {
                    getBot().getMusicManager().loadTrack(message.getTextChannel(), message.getMember().getUser(),
                            "ytsearch:" + String.join(" ", args));
                } else {
                    getBot().getMusicManager().loadTrack(message.getTextChannel(), message.getMember().getUser(), args[0]);
                }
            }
        } else {
            embedBuilder.setDescription("Du bist in keinem Voicechannel ^^");
            message.getTextChannel().sendMessage(embedBuilder.build()).queue();
        }
    }

}
