package de.nmarion.htwbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.nmarion.htwbot.HtwBot;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class MusicManager {

  private final AudioPlayerManager playerManager;
  private final Map<Long, GuildMusicManager> musicManagers;
  private final HtwBot bot;

  /** Constructs a new MusicManager. */
  public MusicManager(HtwBot bot) {
    this.musicManagers = new HashMap<>();
    this.playerManager = new DefaultAudioPlayerManager();
    playerManager.setPlayerCleanupThreshold(TimeUnit.MINUTES.toMillis(3));
    this.bot = bot;
    AudioSourceManagers.registerLocalSource(playerManager);
    AudioSourceManagers.registerRemoteSources(playerManager);
  }

  private synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
    long guildId = guild.getIdLong();
    GuildMusicManager musicManager = musicManagers.get(guildId);
    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
      musicManagers.put(guildId, musicManager);
    }
    return musicManager;
  }

  public void loadTrack(SlashCommandEvent event, String trackUrl) {
    final Guild guild = event.getGuild();
    final User requester = event.getUser();
    loadTrack(guild, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        queue(guild, track);
        final EmbedBuilder eb = getEmbed(guild, requester).setAuthor("Track geladen",
            trackUrl.startsWith("http") ? trackUrl : null, bot.getJDA().getSelfUser().getEffectiveAvatarUrl());
        final AudioTrackInfo trackInfo = track.getInfo();
        final String length;
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
        eb.addField(trackInfo.title, "`" + trackInfo.author + " - " + (trackInfo.isStream ? "STREAM" : length) + "`",
            false);
        event.reply(new MessageBuilder().setEmbeds(eb.build()).build()).queue();
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        final List<AudioTrack> tracks = playlist.getTracks();
        if (playlist.isSearchResult()) {
          trackLoaded(tracks.get(0));
          return;
        }
        final EmbedBuilder eb = getEmbed(guild, requester).setAuthor("Playlist geladen",
            playlist.isSearchResult() ? null : trackUrl, bot.getJDA().getSelfUser().getEffectiveAvatarUrl());
        eb.addField(playlist.getName(), "`" + tracks.size() + " Videos gefunden`", false);
        event.reply(new MessageBuilder().setEmbeds(eb.build()).build()).queue();
        queue(guild, tracks);
        nextTrack(guild, false);
      }

      @Override
      public void noMatches() {
        final Message message = new MessageBuilder().setEmbeds(getEmbed(guild, requester)
            .setAuthor("Nichts gefunden", null, bot.getJDA().getSelfUser().getEffectiveAvatarUrl())
            .addField("Keine Übereinstimmung", "`" + trackUrl + "`", false).build()).build();
        event.reply(message).setEphemeral(true).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        final Message message = new MessageBuilder().setEmbeds(getEmbed(guild, requester)
            .setAuthor("Fehler aufgetreten", null, bot.getJDA().getSelfUser().getEffectiveAvatarUrl())
            .addField("Fehlermeldung", "`" + exception.getMessage() + "`", false).build()).build();
        event.reply(message).setEphemeral(true).queue();
      }
    });
  }

  public void loadTrack(Guild guild, String trackUrl, AudioLoadResultHandler resultHandler) {
    GuildMusicManager musicManager = getGuildMusicManager(guild);
    playerManager.loadItemOrdered(musicManager, trackUrl, resultHandler);
  }

  public void queue(Guild guild, AudioTrack track) {
    getGuildMusicManager(guild).scheduler.queue(track);
  }

  public void queue(Guild guild, Collection<AudioTrack> tracks) {
    getGuildMusicManager(guild).scheduler.getQueue().addAll(tracks);
  }

  public void stop(Guild guild) {
    getGuildMusicManager(guild).scheduler.stop();
  }

  public void nextTrack(Guild guild, boolean interrupt) {
    getGuildMusicManager(guild).scheduler.nextTrack(interrupt);
  }

  public void skip(Guild guild) {
    nextTrack(guild, false);
  }

  public void setVolume(Guild guild, int volume) {
    getGuildMusicManager(guild).player.setVolume(volume);
  }

  public void setPaused(Guild guild, boolean value) {
    getGuildMusicManager(guild).player.setPaused(value);
  }

  public void setRepeat(Guild guild, boolean value) {
    getGuildMusicManager(guild).scheduler.setRepeat(value);
  }

  public void clearQueue(Guild guild) {
    getGuildMusicManager(guild).scheduler.clear();
  }

  public int getVolume(Guild guild) {
    return getGuildMusicManager(guild).player.getVolume();
  }

  public boolean isPaused(Guild guild) {
    return getGuildMusicManager(guild).player.isPaused();
  }

  public boolean isRepeat(Guild guild) {
    return getGuildMusicManager(guild).scheduler.isRepeat();
  }

  public AudioTrack getPlayingTrack(Guild guild) {
    return getGuildMusicManager(guild).player.getPlayingTrack();
  }

  private EmbedBuilder getEmbed(Guild guild, User requester) {
    return new EmbedBuilder();
    // return new EmbedBuilder().setFooter("@" + requester.getUsername() + "#" +
    // requester.getUsername(),
    // requester.getEffectiveAvatarUrl()).setColor(guild.getSelfMember().getColor());
  }
}
