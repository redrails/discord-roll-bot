package com.ihtasham.model;

import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ButtonComponents {
  public static final ItemComponent ROLL_BUTTON = Button.primary("join", "Join");
  public static final ItemComponent LEAVE_BUTTON = Button.secondary("leave", "Leave");
  public static final ItemComponent FINISH_BUTTON = Button.success("finish", "Finish");
  public static final ItemComponent CANCEL_BUTTON = Button.danger("cancel", "Cancel");
}
