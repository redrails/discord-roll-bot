package com.ihtasham.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.concurrent.ConcurrentMap;

import static com.ihtasham.model.Constants.MESSAGE_ID;
import static com.ihtasham.model.Constants.PLAYER_LIST;

public final class DBManager {

  final DB db;
  final ConcurrentMap<String, String> map;

  public DBManager() {
    this.db = DBMaker.memoryDB().make();
    this.map = db.hashMap("map", Serializer.STRING, Serializer.STRING).createOrOpen();
  }

  private void put(String key, String value) {
    map.put(key, value);
  }

  private String get(String key) {
    return map.get(key);
  }

  private boolean containsKey(String key) {
    return map.containsKey(key);
  }

  private void removeKey(String key) {
    map.remove(key);
  }

  public void clearAll() {
    map.clear();
  }

  public void putMessageId(String messageId) {
    this.put(MESSAGE_ID, messageId);
  }

  public boolean messageExists() {
    return this.containsKey(MESSAGE_ID);
  }

  public boolean playersExists() {
    return this.containsKey(PLAYER_LIST);
  }

  public String getMessage() {
    return this.get(MESSAGE_ID);
  }

  public void addPlayer(final String player) {
    map.put(PLAYER_LIST, map.get(PLAYER_LIST) + "," + player);
  }

  public String getPlayers() {
    return map.get(PLAYER_LIST);
  }
}
