package com.ihtasham.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class MessageUtils {
  public static Message sendMessage(final MessageChannel channel, final String message) {
    return channel.sendMessage(message).mapToResult().complete().get();
  }
}
