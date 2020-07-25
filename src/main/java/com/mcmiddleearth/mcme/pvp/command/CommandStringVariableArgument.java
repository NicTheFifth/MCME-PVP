package com.mcmiddleearth.mcme.pvp.command;

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
import java.util.concurrent.CompletableFuture;

public class CommandStringVariableArgument implements ArgumentType<String> {

    Set<String> options;
    public CommandStringVariableArgument() { options = new HashSet<String>(Lists.newArrayList("string"));}

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
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
