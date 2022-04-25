package com.ihtasham.database;

import static com.ihtasham.model.Constants.*;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public final class DBManager {

  final DB db;

  final ConcurrentMap<String, String> map;

  public DBManager() {
    this.db = DBMaker.memoryDB().make();
    this.map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
  }

  private void put(final String guildId, final String key, final String value) {
    map.put(guildId + key, value);
  }

  private String get(final String guildId, final String key) {
    return map.get(guildId + key);
  }

  private boolean containsKey(final String guildId, final String key) {
    return map.containsKey(guildId + key);
  }

  public void clearAllInGuild(final String guildId) {
    map.keySet().stream().filter(k -> k.contains(guildId)).forEach(map::remove);
  }

  public void clearAll() {
    map.clear();
  }

  public void putMessageId(final String guildId, final String messageId) {
    this.put(guildId, MESSAGE_ID, messageId);
  }

  public void createPlayersInMap(final String guildId) {
    this.put(guildId, PLAYER_LIST, "");
  }

  public String getMessageId(final String guildId) {
    return this.get(guildId, MESSAGE_ID);
  }

  public boolean messageExists(final String guildId) {
    return this.containsKey(guildId, MESSAGE_ID);
  }

  public void addPlayer(final String guildId, final String player) {
    this.incrementPlayerCount(guildId, false);
    this.put(guildId, PLAYER_LIST, this.get(guildId, PLAYER_LIST) + "," + player);
  }

  public void removePlayer(final String guildId, final String player) {
    this.incrementPlayerCount(guildId, true);
    final String newPlayers =
        Arrays.stream(this.get(guildId, PLAYER_LIST).split(","))
            .filter(p -> !p.equals(player))
            .collect(Collectors.joining(","));
    this.put(guildId, PLAYER_LIST, newPlayers);
  }

  public int getPlayerCount(final String guildId) {
    try {
      return Integer.parseInt(this.get(guildId, PLAYER_COUNT));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public boolean anyPlayers(final String guildId, final String player) {
    return this.get(guildId, PLAYER_LIST).contains(player);
  }

  private void incrementPlayerCount(final String guildId, final boolean decremenet) {
    try {
      this.put(
          guildId,
          PLAYER_COUNT,
          Integer.toString(
              Integer.parseInt(this.get(guildId, PLAYER_COUNT)) + (decremenet ? -1 : 1)));
    } catch (NumberFormatException e) {
      this.put(guildId, PLAYER_COUNT, "1");
    }
  }

  public String getPlayers(final String guildId) {
    return this.get(guildId, PLAYER_LIST);
  }
}
