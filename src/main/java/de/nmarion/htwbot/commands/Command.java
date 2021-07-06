package de.nmarion.htwbot.commands;

import de.nmarion.htwbot.HtwBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public abstract class Command {

  private final String command;
  private final String description;
  private HtwBot bot;

  public Command(final String command, final String description) {
    this.command = command;
    this.description = description;
  }

  public abstract void register(final CommandListUpdateAction commandListUpdateAction);

  public abstract void execute(final SlashCommandEvent event);

  public void setInstance(final HtwBot instance) {
    if (bot != null) {
      throw new IllegalStateException("Can only initialize once!");
    }
    bot = instance;
  }

  protected EmbedBuilder getEmbed(final SlashCommandEvent event) {
    return new EmbedBuilder()
        .setFooter(
            "@"
                + event.getMember().getUser().getName()
                + "#"
                + event.getMember().getUser().getDiscriminator(),
            event.getMember().getUser().getEffectiveAvatarUrl())
        .setColor(event.getGuild().getSelfMember().getColor());
  }

  protected EmbedBuilder getEmbed(final Member member) {
    return new EmbedBuilder()
        .setFooter(
            "@" + member.getUser().getName() + "#" + member.getUser().getDiscriminator(),
            member.getUser().getEffectiveAvatarUrl())
        .setColor(member.getGuild().getSelfMember().getColor());
  }

  public void say(SlashCommandEvent event, String content) {
    event.reply(content).queue();
  }

  public void saySilent(SlashCommandEvent event, String content) {
    event.reply(content).setEphemeral(true).queue();
  }

  public String getCommand() {
    return command;
  }

  public String getDescription() {
    return description;
  }

  public HtwBot getBot() {
    return bot;
  }
}
