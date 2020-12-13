package de.nmarion.htwbot.commands.music;

import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class SkipCommand extends Command {

    public SkipCommand() {
        super("skip", "Überspringe einen Track");
    }

    @Override
    public void execute(String[] args, Message message) {
        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        if (message.getMember().getVoiceState() != null && message.getMember().getVoiceState().inVoiceChannel()) {
            getBot().getMusicManager().skip(message.getGuild());
            if (getBot().getMusicManager().getPlayingTrack(message.getGuild()) != null) {
                AudioTrackInfo trackInfo = getBot().getMusicManager().getPlayingTrack(message.getGuild()).getInfo();
                String length;
                if (TimeUnit.MILLISECONDS.toHours(trackInfo.length) >= 24) {
                    length = String.format("%dd %02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(trackInfo.length),
                            TimeUnit.MILLISECONDS.toHours(trackInfo.length) % 24,
                            TimeUnit.MILLISECONDS.toMinutes(trackInfo.length) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60);
                } else {
                    length = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(trackInfo.length) % 24,
                            TimeUnit.MILLISECONDS.toMinutes(trackInfo.length) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60);
                }
                embedBuilder.addField(trackInfo.title,
                        "`" + trackInfo.author + " - " + (trackInfo.isStream ? "STREAM" : length) + "`", false);
            } else {
                embedBuilder.setDescription("Es gibt kein weiteres Lied in der Warteschlange");
            }
        } else {
            embedBuilder.setDescription("Du bist in keinem Voicechannel ^^");
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }
}