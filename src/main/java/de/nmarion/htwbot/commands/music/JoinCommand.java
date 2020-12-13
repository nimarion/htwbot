package de.nmarion.htwbot.commands.music;

import de.nmarion.htwbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends Command {

    public JoinCommand() {
        super("join", "Betritt einen Voicechannel");
    }

    @Override
    public void execute(String[] args, Message message) {
        Guild guild = message.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        Member member = message.getMember();
        TextChannel textChannel = message.getTextChannel();

        EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());

        if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
            embedBuilder.setDescription("Der Bot ist bereits verbunden");
        } else if (!member.getVoiceState().inVoiceChannel()) {
            embedBuilder.setDescription("Dafür musst du in einem Voicechannel sein.");
        } else {
            VoiceChannel channel = member.getVoiceState().getChannel();
            guild.getAudioManager().openAudioConnection(channel);
            embedBuilder.setDescription("Channel" + channel.getName() + " betreten");
        }
        textChannel.sendMessage(embedBuilder.build()).queue();

    }
}