package com.ihtasham.Listeners;

import com.ihtasham.model.Emoji;
import com.ihtasham.utils.MessageUtils;
import com.ihtasham.database.DBManager;
import lombok.extern.slf4j.Slf4j;
import com.ihtasham.model.Constants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MessageListener extends ListenerAdapter {

  private final DBManager db;

  public MessageListener(final DBManager db) {
    this.db = db;
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      log.debug("Bot reacted, ignoring event");
      return;
    }

    final String message = event.getMessage().getContentRaw();

    if (message.charAt(0) != Constants.COMMAND_PREFIX) {
      log.debug("Message received was not a command, ignoring event");
      return;
    }

    final String actionableMessage = message.substring(1);

    if (Constants.ROLL_COMMAND.equals(actionableMessage)) {
      if (db.messageExists()) {
        log.warn("A message is already on the stack, cannot create another roll");
        MessageUtils.sendMessage(
            event.getChannel(),
            "A roll is already in progress, please complete it before creating a new one!");
        return;
      }

      final String messageToSend =
          String.format(
              "Press %s to join the queue and then press %s to roll!",
              Emoji.VOTE_EMOJI.getUnicode(), Emoji.STOP_EMOJI.getUnicode());

      final Message sentMessage = MessageUtils.sendMessage(event.getChannel(), messageToSend);

      sentMessage.addReaction(Emoji.VOTE_EMOJI.getUnicode()).complete();
      sentMessage.addReaction(Emoji.STOP_EMOJI.getUnicode()).complete();

      db.putMessageId(sentMessage.getId());
    }
  }
}
