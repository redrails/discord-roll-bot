package database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.concurrent.ConcurrentMap;

import static model.Constants.MESSAGE_ID;

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

  private void clearAll() {
    map.clear();
  }

  public void putMessageId(String messageId) {
    this.put(MESSAGE_ID, messageId);
  }

  public boolean messageExists() {
    return this.containsKey(MESSAGE_ID);
  }

  public String getMessage() {
    return this.get(MESSAGE_ID);
  }

  public void clearMessages() {
    this.clearAll();
  }
}
