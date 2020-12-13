package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class VolumeCommand extends Command {

    public VolumeCommand() {
        super("volume", "Ändert die Lautstärke");
    }

    @Override
    public void execute(String[] args, Message message) {
        Guild guild = message.getGuild();
        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
        if (message.getMember().getVoiceState() != null && message.getMember().getVoiceState().inVoiceChannel()) {
            if (args.length == 1) {
                int volume;
                try {
                    volume = Integer.parseInt(args[0]);
                    if (volume < 0 || volume > 100) {
                        embedBuilder.addField("Ungültige Lautstärke",
                                "Wert kann nur zwischen 0 und 100 gesetzt werden.", false);
                    } else {
                        getBot().getMusicManager().setVolume(guild, volume);
                        embedBuilder.addField("Neue Lautstärke: " + getBot().getMusicManager().getVolume(guild),
                                getVolume(getBot().getMusicManager().getVolume(guild)), false);
                    }
                } catch (NumberFormatException e) {
                    embedBuilder.addField("Ungültiges Angabe", "*" + args[0] + "* ist keine Zahl.", false);
                }
            } else {
                embedBuilder.addField("Lautstärke: " + getBot().getMusicManager().getVolume(guild),
                        getVolume(getBot().getMusicManager().getVolume(guild)), false);
            }
        } else {
            embedBuilder.setDescription("Du bist in keinem Voicechannel ^^");
        }

        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    private String getVolume(int volume) {
        String s = "";
        for (int i = 10; i > 0; i--) {
            if (i > (volume / 10)) {
                s += ":black_large_square:";
            } else {
                s += ":white_large_square:";
            }
        }
        return s;
    }
}