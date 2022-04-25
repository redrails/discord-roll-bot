package com.ihtasham.model;

import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ButtonComponents {
  public static final ItemComponent PLAY_BUTTON = Button.primary("play", "Play");
  public static final ItemComponent ROLL_BUTTON = Button.success("roll", "Roll");
  public static final ItemComponent CANCEL_BUTTON = Button.danger("cancel", "Cancel");
}
