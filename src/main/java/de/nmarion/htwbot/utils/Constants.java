package de.nmarion.htwbot.utils;

import com.google.gson.Gson;
import java.util.Random;
import java.util.regex.Pattern;

public class Constants {

  public static final Gson GSON = new Gson();
  public static final Pattern CODE_BLOCK_REGEX =
      Pattern.compile(
          "```(actionscript3|apache|applescript|asp|brainfuck|c|cfm|clojure|cmake|coffee-script|coffeescript|coffee|cpp|cs|csharp|css|csv|bash|diff|elixir|erb|go|haml|http|java|javascript|json|jsx|less|lolcode|make|markdown|matlab|nginx|objectivec|pascal|PHP|Perl|python|profile|rust|salt|saltstate|shell|sh|zsh|bash|sql|scss|sql|svg|swift|rb|jruby|ruby|smalltalk|vim|viml|volt|vhdl|vue|xml|yaml)?([^`]*)```");

  public static final Random RANDOM = new Random();
}
