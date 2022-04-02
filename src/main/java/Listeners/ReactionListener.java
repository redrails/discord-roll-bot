package Listeners;

import database.DBManager;
import lombok.extern.slf4j.Slf4j;
import model.Emoji;
import model.RollingUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import utils.MessageUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    final Message message = event.retrieveMessage().complete();

    if (!db.messageExists()) {
      log.debug("No message to handle reactions to");
      return;
    }

    if (!db.getMessage().equals(message.getId())) {
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

      List<RollingUser> sortedUsers =
          this.rollNumbers(rollingUsers).stream().sorted().collect(Collectors.toList());

      for (int i = 0; i < sortedUsers.size(); i++) {
        sb.append(
            String.format(
                "%s: `%s` = `%s`\n",
                i + 1, sortedUsers.get(i).getName(), sortedUsers.get(i).getNumber()));
      }

      db.clearMessages();
      MessageUtils.sendMessage(message.getChannel(), sb.toString());
    }

    log.debug("Cannot handle this reaction, doing nothing");
  }

  private List<RollingUser> rollNumbers(List<String> rollingUsers) {
    return generateRoll(
        rollingUsers.stream()
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
