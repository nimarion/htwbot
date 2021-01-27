package de.nmarion.htwbot.commands;

import net.dv8tion.jda.api.entities.Message;

public class GithubCommand extends Command{

    public GithubCommand(String command, String description) {
        super("github", "Schickt den Github Link des Bots");
    }

    @Override
    public void execute(String[] args, Message message) {
        message.getTextChannel().sendMessage("https://github.com/nimarion/htwbot").queue();
    }
    
}
