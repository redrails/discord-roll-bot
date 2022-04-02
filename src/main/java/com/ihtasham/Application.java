package com.ihtasham;

import com.ihtasham.Listeners.MessageListener;
import com.ihtasham.Listeners.ReactionListener;
import com.ihtasham.database.DBManager;
import lombok.extern.slf4j.Slf4j;
import com.ihtasham.model.Constants;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Application {

  public static void main(String[] args) {
    try {
      Properties props = getProperties();
      DBManager db = new DBManager();
      JDABuilder.createDefault(props.getProperty("app.token"))
          .addEventListeners(new ReactionListener(db), new MessageListener(db))
          .build();

    } catch (LoginException e) {
      log.error("Login to the Discord API failed.");
    } catch (Exception e) {
      log.error("Something went wrong: {}", e.getMessage());
      System.exit(1);
    }
  }

  static Properties getProperties() throws Exception {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try (InputStream is = loader.getResourceAsStream(Constants.CONFIG_FILE_NAME)) {
      props.load(is);
      return props;
    }
  }
}
