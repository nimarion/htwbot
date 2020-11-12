package de.nmarion.htwbot.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

public class VerifyUtils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX;
    private static JsonObject MAIL_JSON_OBJECT;

    static {
        VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE);
        try {
            MAIL_JSON_OBJECT = JsonParser.parseReader(new FileReader("data/mails.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkMailFormat(final String mail) {
        final Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        if (matcher.matches()) {
            final String mailAddress = matcher.group(0);
            final boolean validDomain = mailAddress.endsWith("htw-saarland.de");
            final boolean validStart = (mailAddress.startsWith("ki") ^ mailAddress.startsWith("pib"));
            return validDomain && validStart;
        }
        return false;
    }

    public static boolean mailExists(final String mail) {
        if (MAIL_JSON_OBJECT == null) {
            return false;
        }
        return MAIL_JSON_OBJECT.get(mail) != null;
    }

    public static String getNameFromMail(final String mail) {
        return mailExists(mail) ? MAIL_JSON_OBJECT.get(mail).getAsString() : null;
    }
    
}
