package de.nmarion.htwbot.commands;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public class VerifyCommand extends Command {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public VerifyCommand() {
        super("verify", "Startet die Verifizierung von deinem Discord Account");
    }

    @Override
    public void execute(String[] args, Message message) {
        final Role piRole = message.getGuild().getRolesByName("Praktische Informatik", true).get(0);
        final Role kiRole = message.getGuild().getRolesByName("Kommunikationsinformatik", true).get(0);
        final EmbedBuilder embedBuilder = getEmbed(message.getMember());
        if (message.getMember().getRoles().contains(piRole) || message.getMember().getRoles().contains(kiRole)) {
            embedBuilder.setDescription("Du bist bereits verifiziert :wink:");
        } else {
            if (args.length > 0) {
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(args[0]);
                if (matcher.matches()) {
                    final String mailAddress = matcher.group(0);
                    if ((mailAddress.startsWith("ki") ^ mailAddress.startsWith("pib"))
                            && mailAddress.endsWith("htw-saarland.de")) {
                        embedBuilder.setDescription(sendMail(message.getMember(), mailAddress));
                    } else {
                        embedBuilder.setDescription("Bitte nutze deine pib/ki.vorname.nachname@htw-saarland.de Email");
                    }
                } else {
                    embedBuilder.setDescription("Bitte gebe eine richtige Email an");
                }
            } else {
                embedBuilder.setDescription("!verify pib/ki.vorname.nachname@htw-saarland.de");
            }
        }
        message.getTextChannel().sendMessage(embedBuilder.build()).queue(success -> {
            success.delete().queueAfter(20, TimeUnit.SECONDS);
            message.delete().queueAfter(10, TimeUnit.SECONDS);
        });
    }

    private String sendMail(final Member member, final String mail) {
        if (getBot().getVerifyCodes().containsKey(member)) {
            return "Du hast bereits eine Verifzierung gestartet.\nBitte überprüfe dein Postfach auf neues Mails";
        }
        final Integer randomCode = 100000 + Constants.RANDOM.nextInt(900000);
        Email email = new SimpleEmail();
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator(Configuration.MAIL_ADDRESS, Configuration.MAIL_PASSWORD));
        email.setDebug(false);
        email.setHostName(Configuration.MAIL_HOSTNAME);
        email.setSubject("Discord Bestätiguns Code");
        try {
            email.setFrom(Configuration.MAIL_ADDRESS);
            email.setMsg("Deine Bestätigunscode ist " + randomCode);
            email.addTo(mail);
            email.setStartTLSEnabled(true);
            email.send();
            getBot().getVerifyCodes().put(member, new VerifyPerson(mail, randomCode));
            return "Der Bestätigunscode wurde versendet! Bitte überprüfe dein Postfach";
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
