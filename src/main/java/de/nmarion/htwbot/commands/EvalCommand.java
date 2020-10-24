package de.nmarion.htwbot.commands;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.regex.Matcher;

import com.google.gson.JsonObject;

import de.nmarion.htwbot.Configuration;
import de.nmarion.htwbot.utils.Constants;
import de.nmarion.htwbot.utils.DiscordUtils;
import de.nmarion.htwbot.utils.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EvalCommand extends Command {

    private OkHttpClient httpClient;

    public EvalCommand() {
        super("eval", "Führt Code in einem Online Compiler aus");
        this.httpClient = new OkHttpClient().newBuilder().callTimeout(Duration.ofMinutes(2))
                .connectTimeout(Duration.ofMinutes(2)).readTimeout(Duration.ofMinutes(2))
                .writeTimeout(Duration.ofMinutes(2)).build();
    }

    @Override
    public void execute(String[] args, Message message) {
        final Matcher matcher = Constants.CODE_BLOCK_REGEX.matcher(String.join(" ", args));
        if (matcher.matches()) {
            final String code = matcher.group(2).trim();
            final Language language = Language.valueOf(matcher.group(1).toUpperCase());

            try {
                final String response = runJDoodle(language, code);
                final EmbedBuilder embedBuilder = DiscordUtils.getDefaultEmbed(message.getMember());
                if (response != null) {
                    JsonObject jsonObject = Constants.GSON.fromJson(response, JsonObject.class);
                    final String description = "```" + jsonObject.get("output").getAsString() + "```";
                    if(description.length() < MessageEmbed.TEXT_MAX_LENGTH){
                        embedBuilder.appendDescription(description);    
                    } else {
                        embedBuilder.appendDescription("Ausgabe ist zu groß!\n Discord erlaubt maximal " + MessageEmbed.TEXT_MAX_LENGTH + " Zeichen");
                    }
                } else {
                    embedBuilder.appendDescription("Es gab einen Fehler");
                }
                message.getTextChannel().sendMessage(embedBuilder.build()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String runJDoodle(final Language language, final String code) throws IOException {
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("clientId", Configuration.JDOODLE_CLIEN_ID);
        hashMap.put("clientSecret", Configuration.JDOODLE_CLIENT_SECRET);
        hashMap.put("script", code);
        hashMap.put("language", language.getLang());
        hashMap.put("versionIndex", language.getCode());

        final Request request = new Request.Builder().url("https://api.jdoodle.com/v1/execute")
                .method("POST", RequestBody.create(MediaType.parse("application/json"), Constants.GSON.toJson(hashMap)))
                .build();
        final Response response = httpClient.newCall(request).execute();
        if (response.code() != 200) {
            return null;
        }
        return response.body().string();

    }

}
