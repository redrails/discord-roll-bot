package com.ihtasham.database;

import static com.ihtasham.model.Constants.MESSAGE_ID;
import static com.ihtasham.model.Constants.PLAYER_LIST;

import java.util.concurrent.ConcurrentMap;
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

  private void removeKey(final String guildId, final String key) {
    map.remove(guildId + key);
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

  public boolean messageExists(final String guildId) {
    return this.containsKey(guildId, MESSAGE_ID);
  }

  public boolean playersExists(final String guildId) {
    return this.containsKey(guildId, PLAYER_LIST);
  }

  public String getMessage(final String guildId) {
    return this.get(guildId, MESSAGE_ID);
  }

  public void addPlayer(final String guildId, final String player) {
    map.put(guildId + PLAYER_LIST, map.get(PLAYER_LIST) + "," + player);
  }

  public String getPlayers(final String guildId) {
    return map.get(guildId + PLAYER_LIST);
  }
}
