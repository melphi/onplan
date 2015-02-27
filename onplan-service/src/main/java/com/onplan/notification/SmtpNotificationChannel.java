package com.onplan.notification;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public class SmtpNotificationChannel implements NotificationChannel {
  private static final Logger LOGGER = Logger.getLogger(SmtpNotificationChannel.class);

  private final String host;
  private final String username;
  private final String password;
  private final int port;
  private final boolean useSsl;

  @Inject
  @Named("email.from")
  private String from;

  @Inject
  @Named("email.to")
  private String to;

  @Inject
  public SmtpNotificationChannel(
      @Named("smtp.host") String host,
      @Named("smtp.username") String username,
      @Named("smtp.password") String password,
      @Named("smtp.port") String port,
      @Named("smtp.useSsl") String useSsl) {
    this.host = checkNotNullOrEmpty(host);
    this.username = checkNotNullOrEmpty(username);
    this.password = checkNotNullOrEmpty(password);
    this.port = Integer.valueOf(port);
    this.useSsl = Boolean.valueOf(useSsl);
  }

  @Override
  public void notifyMessage(String title, String body) throws Exception {
    LOGGER.info(
        String.format("Sending email (SMTP) notification to [%s]. Subject: [%s]", to, title));
    createEmail(title, body).send();
  }

  @Override
  public boolean isActive() {
    return true;
  }

  private Email createEmail(final String title, final String body) throws EmailException {
    Email email = new SimpleEmail();
    email.setHostName(host);
    email.setAuthentication(username, password);
    email.setSmtpPort(port);
    email.setSSLOnConnect(useSsl);
    email.setFrom(checkNotNullOrEmpty(from));
    email.addTo(checkNotNullOrEmpty(to));
    email.setSubject(title);
    email.setMsg(body);
    return email;
  }
}
