package de.nmarion.htwbot.listener.message;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.HtwBot;
import de.nmarion.htwbot.utils.Constants;
import de.nmarion.htwbot.utils.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceiveListener extends ListenerAdapter {

    private final HtwBot bot;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
            .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public MessageReceiveListener(final HtwBot bot) {
        this.bot = bot;
        bot.getJDA().addEventListener(this);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getChannel().getName().equals("verifizieren")) {
            final Member member = event.getMember();
            final Role piRole = bot.getGuild().getRolesByName("Praktische Informatik", true).get(0);
            final Role kiRole = bot.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0);
            if (member.getRoles().contains(piRole) || member.getRoles().contains(kiRole)) {
                return;
            }
            try {
                final Integer code = Integer.valueOf(event.getMessage().getContentRaw());

                if (bot.getVerifyCodes().containsKey(member)) {
                    final VerifyPerson verifyPerson = bot.getVerifyCodes().get(member);
                    if (code.equals(verifyPerson.getRandomCode())) {
                        member.getGuild()
                                .addRoleToMember(member, verifyPerson.getMail().startsWith("ki") ? kiRole : piRole)
                                .queue(success -> bot.getVerifyCodes().remove(member));
                    } else {
                        event.getChannel().sendMessage("Bitte überprüfe deinen Code!").queue(success -> {
                            success.delete().queueAfter(10, TimeUnit.SECONDS);
                        });
                    }
                }

            } catch (NumberFormatException exception) {
                verifyMail(event, event.getMessage().getContentRaw());
            } finally {
                event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
            }
        }
    }

    private void verifyMail(final GuildMessageReceivedEvent event, String mail) {
        final Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        final EmbedBuilder embedBuilder = DiscordUtils.getDefaultEmbed(event.getMember());

        if (matcher.matches()) {
            final String mailAddress = matcher.group(0);
            final boolean validDomain = mailAddress.endsWith("htw-saarland.de");
            final boolean validStart = (mailAddress.startsWith("ki") ^ mailAddress.startsWith("pib"));
            if (validStart && validDomain) {
                embedBuilder.setDescription(sendMail(event.getMember(), mailAddress));
            } else {
                if(!validDomain){
                    embedBuilder.setDescription("Deine Email muss mit @htw-saarland.de enden");
                } else if(!validStart){
                    embedBuilder.setDescription("Deine Email muss entweder mit pib (Praktische Informatik) **oder** ki (Kommunikationsinformatik) starten");
                } else {
                    embedBuilder.setDescription("Bitte nutze deine pib/ki.vorname.nachname@htw-saarland.de Email");
                }
            }
        } else {
            embedBuilder.setDescription("Bitte gebe eine richtige Email an");
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue(success -> success.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    private String sendMail(final Member member, final String mail) {
        if (bot.getVerifyCodes().containsKey(member)) {
            return "Du hast bereits eine Verifzierung gestartet.\nBitte überprüfe dein Postfach auf neues Mails";
        }
        final Integer randomCode = 100000 + Constants.RANDOM.nextInt(900000);
        Email email = new SimpleEmail();
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator(Configuration.MAIL_ADDRESS, Configuration.MAIL_PASSWORD));
        email.setDebug(false);
        email.setHostName(Configuration.MAIL_HOSTNAME);
        email.setSubject("Discord Code");
        try {
            email.setFrom(Configuration.MAIL_ADDRESS);
            email.setMsg("Dein Bestätigungscode ist " + randomCode);
            email.addTo(mail);
            email.setStartTLSEnabled(true);
            email.send();
            bot.getVerifyCodes().put(member, new VerifyPerson(mail, randomCode));
            return "Der Code wurde versendet! Bitte überprüfe dein Postfach\nBei Problemen: <#771308490676895774>";
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
