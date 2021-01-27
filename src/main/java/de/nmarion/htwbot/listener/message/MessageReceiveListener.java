package de.nmarion.htwbot.listener.message;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.utils.Constants;
import de.nmarion.htwbot.utils.DiscordUtils;
import de.nmarion.htwbot.utils.VerifyUtils;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MessageReceiveListener extends ListenerAdapter {

  private final HtwBot bot;

  public MessageReceiveListener(final HtwBot bot) {
    this.bot = bot;
    bot.getJDA().addEventListener(this);
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot()
        || !event.getChannel().getName().equals("verifizieren")
        || checkRoles(event.getMember())) {
      return;
    }
    try {
      checkCode(Integer.valueOf(event.getMessage().getContentRaw()), event.getMessage());
    } catch (NumberFormatException exception) {
      final EmbedBuilder embedBuilder = DiscordUtils.getDefaultEmbed(event.getMember());
      if (VerifyUtils.checkMailFormat(event.getMessage().getContentRaw())) {
        if (VerifyUtils.mailExists(event.getMessage().getContentRaw())) {
          if (bot.getVerifyCodes().containsKey(event.getMember())) {
            embedBuilder.setDescription(
                "Du hast bereits eine Verifzierung gestartet.\n"
                    + "Bitte überprüfe dein Postfach auf neues Mails");
          } else {
            embedBuilder.setDescription(
                sendMail(event.getMember(), event.getMessage().getContentRaw()));
          }
        } else {
          embedBuilder.setDescription(
              "Diese Email existiert nicht oder wurde falsch angegeben. \n"
                  + "**pib/ki.vorname.nachname@htw-saarland.de**");
        }
      } else {
        embedBuilder.setDescription(
            "Bitte gebe eine richtige Email an\n**pib/ki.vorname.nachname@htw-saarland.de**");
      }
      event
          .getChannel()
          .sendMessage(embedBuilder.build())
          .queue(success -> success.delete().queueAfter(10, TimeUnit.SECONDS));
    } finally {
      event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
    }
  }

  private boolean checkRoles(final Member member) {
    return member.getRoles().stream()
        .filter(
            role ->
                role.getName().equals("Praktische Informatik")
                    || role.getName().equals("Kommunikationsinformatik"))
        .findAny()
        .isPresent();
  }

  private void checkCode(final Integer code, final Message message) {
    final Member member = message.getMember();
    if (bot.getVerifyCodes().containsKey(member)) {
      final VerifyPerson verifyPerson = bot.getVerifyCodes().get(member);
      if (code.equals(verifyPerson.getRandomCode())) {
        final Role piRole = bot.getGuild().getRolesByName("Praktische Informatik", true).get(0);
        final Role kiRole = bot.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0);
        member
            .getGuild()
            .modifyNickname(member, VerifyUtils.getNameFromMail(verifyPerson.getMail()))
            .queue();
        member
            .getGuild()
            .addRoleToMember(member, verifyPerson.getMail().startsWith("ki") ? kiRole : piRole)
            .queue(success -> bot.getVerifyCodes().remove(member));
      } else {
        message
            .getChannel()
            .sendMessage("Bitte überprüfe deinen Code!")
            .queue(
                success -> {
                  success.delete().queueAfter(10, TimeUnit.SECONDS);
                });
      }
    }
  }

  private String sendMail(final Member member, final String mail) {
    final Integer randomCode = 100000 + Constants.RANDOM.nextInt(900000);
    try {
      final Email email = new SimpleEmail().setSubject("Discord Code").setStartTLSEnabled(true);
      email.setSmtpPort(587);
      email.setAuthenticator(
          new DefaultAuthenticator(Configuration.MAIL_ADDRESS, Configuration.MAIL_PASSWORD));
      email.setDebug(false);
      email.setHostName(Configuration.MAIL_HOSTNAME);
      email.setMsg("Dein Bestätigungscode ist " + randomCode);
      email.addTo(mail);
      email.setFrom(Configuration.MAIL_ADDRESS);
      email.send();
      bot.getVerifyCodes().put(member, new VerifyPerson(mail, randomCode));
      return "Der Code wurde versendet! Bitte überprüfe dein Postfach\n"
          + "Bei Problemen: <#771308490676895774>";
    } catch (EmailException e) {
      e.printStackTrace();
      return "Es gab einen Fehler beim versenden der Mail";
    }
  }

  public class VerifyPerson {

    private String mail;
    private Integer randomCode;

    public VerifyPerson(String mail, Integer randomCode) {
      this.mail = mail;
      this.randomCode = randomCode;
    }

    public String getMail() {
      return mail;
    }

    public Integer getRandomCode() {
      return randomCode;
    }
  }
}
