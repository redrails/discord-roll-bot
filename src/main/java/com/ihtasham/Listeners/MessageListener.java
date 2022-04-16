package com.ihtasham.Listeners;

import com.ihtasham.database.DBManager;
import com.ihtasham.model.Constants;
import com.ihtasham.model.Emoji;
import com.ihtasham.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
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

    final String guildId = event.getGuild().getId();
    final String message = event.getMessage().getContentRaw();

    if (message.isEmpty() || message.isBlank()) {
      log.warn("The message was empty or blank, ignoring event");
      return;
    }

    if (guildId.isEmpty() || guildId.isBlank()) {
      log.warn("The guildId was empty or blank, ignoring event");
      return;
    }

    if (message.charAt(0) != Constants.COMMAND_PREFIX) {
      log.debug("Message received was not a command, ignoring event");
      return;
    }

    final String actionableMessage = message.substring(1).split(" ")[0];

    try {

      if (Constants.ROLL_COMMAND.equals(actionableMessage)) {

        if (db.messageExists(guildId)) {
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

        db.putMessageId(guildId, sentMessage.getId());
      }

      if (Constants.ADD_COMMAND.equals(actionableMessage) && db.messageExists(guildId)) {
        event
            .getMessage()
            .getMentionedMembers()
            .forEach(m -> db.addPlayer(guildId, m.getEffectiveName()));
        event.getMessage().addReaction(Emoji.CHECK_EMOJI.getUnicode()).complete();
      }

      if (Constants.HELP_COMMAND.equals(actionableMessage)) {
        getHelpMessage(event);
      }

      if (Constants.MEM_RESET_COMMAND.equals(actionableMessage)) {
        db.clearAll();
        event.getMessage().addReaction(Emoji.CHECK_EMOJI.getUnicode()).complete();
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private void getHelpMessage(MessageReceivedEvent event) {
    log.warn("Generating help message");
    final String message =
        String.format(
            "Help: Type `!cs` to create a new roll, type `!add @player_name` to add players to the roll and click the %s emoji to roll the numbers!",
            Emoji.STOP_EMOJI.getUnicode());
    MessageUtils.sendMessage(event.getChannel(), message);
  }
}
