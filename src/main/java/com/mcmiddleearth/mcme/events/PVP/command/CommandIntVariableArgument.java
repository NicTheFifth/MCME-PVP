package com.mcmiddleearth.mcme.events.PVP.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class CommandIntVariableArgument implements ArgumentType<String> {

    Set<String> options;
    public CommandIntVariableArgument() { options = new HashSet<String>(Lists.newArrayList("integer"));}

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readString();
        try {
            Integer.parseInt(o);
            return o;
        }
        catch(Exception e){
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing during action evaluation")), new LiteralMessage("Failed parsing during action evaluation on action:" + o));
        }
    }
    @Override
    public Collection<String> getExamples() {
        return options;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        for (String option : options) {
            if (option.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(option);
            }
        }
        return builder.buildFuture();
    }
}
