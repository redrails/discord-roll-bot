package com.ihtasham.Listeners;

import com.ihtasham.database.DBManager;
import com.ihtasham.model.Emoji;
import com.ihtasham.model.RollingUser;
import com.ihtasham.utils.MessageUtils;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ReactionListener extends ListenerAdapter {

  final DBManager db;

  public ReactionListener(final DBManager db) {
    this.db = db;
  }

  @Override
  public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
    if (event.retrieveUser().complete().isBot()) {
      log.debug("Bot reacted, ignoring event");
      return;
    }

    final String guildId = event.getGuild().getId();
    final Message message = event.retrieveMessage().complete();

    if (!db.messageExists(guildId)) {
      log.debug("No message to handle reactions to");
      return;
    }

    if (!db.getMessage(guildId).equals(message.getId())) {
      log.debug("Cannot handle reaction for this message {}", message.getId());
      return;
    }

    if (event.getReaction().getReactionEmote().getEmoji().equals(Emoji.STOP_EMOJI.getUnicode())) {
      Optional<MessageReaction> reactionsOptional =
          message.getReactions().stream()
              .filter(r -> r.getReactionEmote().getEmoji().equals(Emoji.VOTE_EMOJI.getUnicode()))
              .findFirst();

      if (reactionsOptional.isEmpty()) {
        log.warn("Cannot handle reactions on this message");
        return;
      }

      StringBuilder sb = new StringBuilder();
      sb.append("The final roll results are: \n");

      List<String> rollingUsers =
          reactionsOptional.get().retrieveUsers().complete().stream()
              .filter(u -> !u.isBot())
              .map(User::getName)
              .collect(Collectors.toList());

      if (db.playersExists(guildId)) {
        rollingUsers.addAll(
            Arrays.stream(db.getPlayers(guildId).split(",")).skip(1).collect(Collectors.toList()));
      }

      List<RollingUser> sortedUsers =
          this.rollNumbers(rollingUsers).stream().sorted().collect(Collectors.toList());

      if (sortedUsers.size() < 1) {
        MessageUtils.sendMessage(
            message.getChannel(), "Roll cancelled: No members were part of this roll.");
      }

      for (int i = 0; i < sortedUsers.size(); i++) {
        sb.append(
            String.format(
                "%s: `%s` = `%s`\n",
                i + 1, sortedUsers.get(i).getName(), sortedUsers.get(i).getNumber()));
      }

      db.clearAllInGuild(guildId);
      MessageUtils.sendMessage(message.getChannel(), sb.toString());
    }

    log.debug("Cannot handle this reaction, doing nothing");
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
