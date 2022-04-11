package com.ihtasham.Listeners;

import com.ihtasham.model.Emoji;
import com.ihtasham.utils.MessageUtils;
import com.ihtasham.database.DBManager;
import lombok.extern.slf4j.Slf4j;
import com.ihtasham.model.Constants;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    final String actionableMessage = message.substring(1).split(" ")[0];

    try {

      if (Constants.ROLL_COMMAND.equals(actionableMessage)) {
        this.checkMessageOnStack(event);

        final String messageToSend =
            String.format(
                "Press %s to join the queue and then press %s to roll!",
                Emoji.VOTE_EMOJI.getUnicode(), Emoji.STOP_EMOJI.getUnicode());

        final Message sentMessage = MessageUtils.sendMessage(event.getChannel(), messageToSend);

        sentMessage.addReaction(Emoji.VOTE_EMOJI.getUnicode()).complete();
        sentMessage.addReaction(Emoji.STOP_EMOJI.getUnicode()).complete();

        db.putMessageId(sentMessage.getId());
      }

      if (Constants.ADD_COMMAND.equals(actionableMessage) && db.messageExists()) {
        event.getMessage().getMentionedMembers().forEach(m -> db.addPlayer(m.getEffectiveName()));
        event.getMessage().addReaction(Emoji.CHECK_EMOJI.getUnicode()).complete();
      }

      if (Constants.HELP_COMMAND.equals(actionableMessage)) {
        getHelpMessage(event);
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private void checkMessageOnStack(MessageReceivedEvent event) throws Exception {
    if (db.messageExists()) {
      log.warn("A message is already on the stack, cannot create another roll");
      MessageUtils.sendMessage(
          event.getChannel(),
          "A roll is already in progress, please complete it before creating a new one!");
      throw new Exception("Message already on stack");
    }
  }

  private void getHelpMessage(MessageReceivedEvent event) throws Exception {
    log.warn("Generating help message");
    final String message =
        String.format(
            "Help: Type `!cs` to create a new roll, type `!add @player_name` to add players to the roll and click the %s emoji to roll the numbers!",
            Emoji.STOP_EMOJI.getUnicode());
    MessageUtils.sendMessage(event.getChannel(), message);
    throw new Exception("Message already on stack");
  }
}
