package model;

public enum Emoji {
  VOTE_EMOJI("\uD83D\uDC4D"),
  STOP_EMOJI("\uD83C\uDFB2");

  private final String unicode;

  Emoji(final String unicode) {
    this.unicode = unicode;
  }

  public String getUnicode() {
    return this.unicode;
  }
}
