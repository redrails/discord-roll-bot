package com.ihtasham.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

public class MessageUtils {
  public static Message sendMessage(final MessageChannel channel, final String message) {
    return channel.sendMessage(message).mapToResult().complete().get();
  }

  public static Message sendMessageEmbeds(
      final MessageChannel channel, final MessageEmbed messageEmbed) {
    return channel.sendMessageEmbeds(messageEmbed).complete();
  }

  public static Message sendMessageEmbedsWithActionRows(
      final MessageChannel channel, final MessageEmbed messageEmbed, ItemComponent... components) {
    return channel.sendMessageEmbeds(messageEmbed).setActionRow(components).complete();
  }
}
