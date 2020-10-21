package de.nmarion.htwbot.listener.voice;

import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.tempchannel.Tempchannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildVoiceListener extends ListenerAdapter {

    private final HtwBot bot;

    public GuildVoiceListener(final HtwBot bot) {
        this.bot = bot;
        bot.getJDA().addEventListener(this);
    }

    @Override
    public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
        bot.getTempchannels().get(event.getChannelJoined().getId()).onTempchannelJoin(event.getChannelJoined(),
                event.getMember());
    }

    @Override
    public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
        bot.getTempchannels().get(event.getChannelJoined().getId()).onTempchannelJoin(event.getChannelJoined(),
                event.getMember());
        bot.getTempchannels().get(event.getChannelLeft().getId()).onTempchannelLeave(event.getChannelLeft(),
                event.getMember());
    }

    @Override
    public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
        bot.getTempchannels().get(event.getChannelLeft().getId()).onTempchannelLeave(event.getChannelLeft(),
                event.getMember());
    }

    @Override
    public void onVoiceChannelCreate(final VoiceChannelCreateEvent event) {
        bot.getTempchannels().put(event.getChannel().getId(), new Tempchannel());
    }

    @Override
    public void onVoiceChannelDelete(final VoiceChannelDeleteEvent event) {
        bot.getTempchannels().remove(event.getChannel().getId());
    }
}