package de.nmarion.htwbot.utils;

public enum Language {

    BASH("bash", 3, "shell"),
    SH("bash", 3, "shell"),
    BRAINFUCK("brainfuck", 0, "Brainf**k"),
    BF("brainfuck", 0, "Brainf**k"),
    C("c", 4, "CLang"),
    COFFEESCRIPT("coffeescript", 3, "CoffeeScript"),
    CPP("cpp14", 4, "C++"),
    CS("csharp", 3, "C#"),
    CSHARP("csharp", 3, "C#"),
    DART("dart", 3, "Dart"),
    ELIXIR("elixir", 3, "Elixir"),
    GOLANG("go", 3, "Go"),
    GO("go", 3, "Go"),
    HS("haskell", 3, "Haskell"),
    HASKELL("haskell", 3, "Haskell"),
    JAVA("java", 3, "Java"),
    KOTLIN("kotlin", 2, "Kotlin"),
    KT("kotlin", 2, "Kotlin"),
    LUA("lua", 2, "Lua"),
    JAVASCRIPT("nodejs", 3, "JavaScript"),
    JS("nodejs", 3, "JavaScript"),
    NODE("nodejs", 3, "NodeJS"),
    NODEJS("nodejs", 3, "NodeJS"),
    PASCAL("pascal", 2, "Pascal"),
    PERL("perl", 3, "Perl"),
    PHP("php", 3, "PHP"),
    PYTHON("python3", 3, "Python"),
    PY("python3", 3, "Python"),
    RUBY("ruby", 3, "Ruby"),
    RUST("rust", 3, "RUST"),
    SCALA("scala", 3, "Scala"),
    SQL("sql", 3, "SQL"),
    SWIFT("swift", 3, "Swift");

    private final String lang;
    private final Integer code;
    private final String humanReadable;

    Language(String lang, Integer code, String humanReadable){
        this.lang = lang;
        this.code = code;
        this.humanReadable = humanReadable;
    }

    /**
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return the humanReadable
     */
    public String getHumanReadable() {
        return humanReadable;
    }

    
    
}
