package com.ihtasham.Listeners;

import com.ihtasham.database.DBManager;
import com.ihtasham.model.RollingUser;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter {

  final DBManager db;

  public ButtonListener(final DBManager db) {
    this.db = db;
  }

  @Override
  public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

    final String guildId = event.getGuild().getId();
    final User user = event.getInteraction().getUser();
    final String userName = event.getInteraction().getUser().getName();

    if (Objects.equals(event.getComponent().getId(), "play")) {
      if (!db.anyPlayers(guildId, userName)) {
        db.addPlayer(guildId, userName);
        updateMessageEmbed(event, String.format("\n- %s", user));
      } else {
        event.deferEdit().complete();
        return;
      }
    }

    if (Objects.equals(event.getComponent().getId(), "leave")) {
      if (db.anyPlayers(guildId, userName)) {
        db.removePlayer(guildId, userName);
        removeLineFromEmbed(event, String.format("\n- %s", user));
      } else {
        event.deferEdit().complete();
        return;
      }
    }

    if (Objects.equals(event.getComponent().getId(), "cancel")) {
      event
          .getMessage()
          .editMessage(String.format("Roll was cancelled by %s!", event.getInteraction().getUser()))
          .override(true)
          .complete();
      db.clearAllInGuild(guildId);
      return;
    }

    if (Objects.equals(event.getComponent().getId(), "roll")) {

      if (db.getPlayerCount(guildId) < 1) {
        event
            .reply(
                String.format(
                    "%s - You cannot roll without any players in the queue!",
                    event.getInteraction().getUser()))
            .complete();
        return;
      }

      StringBuilder sb = new StringBuilder();
      sb.append(
          String.format(
              "%s players rolled, and the final results are: \n", db.getPlayerCount(guildId)));

      List<String> rollingUsers =
          Arrays.stream(db.getPlayers(guildId).split(","))
              .skip(1)
              .filter(p -> !p.isBlank())
              .collect(Collectors.toList());

      List<RollingUser> sortedUsers =
          this.rollNumbers(rollingUsers).stream().sorted().collect(Collectors.toList());

      if (sortedUsers.size() < 1) {
        event
            .reply(
                String.format(
                    "%s - You cannot roll without any players in the queue!",
                    event.getInteraction().getUser()))
            .complete();
        return;
      } else {
        for (int i = 0; i < sortedUsers.size(); i++) {
          sb.append(
              String.format(
                  "%s: @%s = `%s`\n",
                  i + 1, sortedUsers.get(i).getName(), sortedUsers.get(i).getNumber()));
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Roll is complete, glhf!");
        eb.setColor(Color.GREEN);
        eb.setDescription(sb.toString());
        eb.setFooter(
            "Type !cs to start again", event.getMessage().getAuthor().getEffectiveAvatarUrl());

        event.reply("").addEmbeds(eb.build()).complete();
      }
      db.clearAllInGuild(guildId);
      event
          .getMessage()
          .editMessage(String.format("Roll was completed by %s!", event.getInteraction().getUser()))
          .override(true)
          .complete();
    }
  }

  private void updateMessageEmbed(final ButtonInteractionEvent event, final String message) {
    final EmbedBuilder messageEmbed = new EmbedBuilder(event.getMessage().getEmbeds().get(0));
    messageEmbed.appendDescription(message);
    event.editMessageEmbeds(messageEmbed.build()).queue();
  }

  private void removeLineFromEmbed(final ButtonInteractionEvent event, final String message) {
    final EmbedBuilder messageEmbed = new EmbedBuilder(event.getMessage().getEmbeds().get(0));
    final String originalMessage = messageEmbed.getDescriptionBuilder().toString();
    final String newMessage = originalMessage.replace(message, "");
    messageEmbed.setDescription(newMessage);
    event.editMessageEmbeds(messageEmbed.build()).queue();
  }

  private List<RollingUser> rollNumbers(List<String> rollingUsers) {
    return generateRoll(
        rollingUsers.stream()
            .distinct()
            .map(u -> RollingUser.builder().name(u).build())
            .collect(Collectors.toList()));
  }

  private List<RollingUser> generateRoll(List<RollingUser> rollingUsers) {
    return rollingUsers.stream()
        .map(
            r ->
                RollingUser.builder()
                    .name(r.getName())
                    .number(new Random().nextInt(100) + 1)
                    .build())
        .collect(Collectors.toList());
  }
}
