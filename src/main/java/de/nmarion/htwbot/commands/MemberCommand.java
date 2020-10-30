package de.nmarion.htwbot.commands;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class MemberCommand extends Command {

    private static final String DESCRIPTION_PATTERN = "***%s*** (%d)\n";

    public MemberCommand() {
        super("mitglieder", "Zeigt die Rollen Verteilung der Mitglieder an");
    }

    @Override
    public void execute(String[] args, Message message) {
        final EmbedBuilder embedBuilder = getEmbed(message.getMember()).setTitle("Mitglieder Statistik");
        message.getGuild().loadMembers().onSuccess(success -> {
            final Map<Role, Long> roles = success.stream()
                    .filter(member -> PermissionUtil.canInteract(message.getGuild().getSelfMember(), member))
                    .map(member -> member.getRoles())
                    .flatMap(stream -> stream.stream().filter(role -> (!role.isManaged() && role.getName() != "Bot")))
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            roles.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .map(role -> String.format(DESCRIPTION_PATTERN, role.getName(), roles.get(role)))
                    .forEach(embedBuilder::appendDescription);

            message.getChannel().sendMessage(embedBuilder.build()).queue();
            ;
        }).onError(error -> {
            embedBuilder.setDescription("Es gab einen Discord Fehler :/");
            message.getChannel().sendMessage(embedBuilder.build()).queue();
            ;
        });

    }

}
