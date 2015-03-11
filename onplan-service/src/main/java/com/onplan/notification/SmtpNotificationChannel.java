package com.onplan.notification;

import com.onplan.domain.persistent.AlertEvent;
import com.onplan.domain.persistent.SystemEvent;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public final class SmtpNotificationChannel implements NotificationChannel {
  private static final Logger LOGGER = Logger.getLogger(SmtpNotificationChannel.class);

  private final String host;
  private final String username;
  private final String password;
  private final int port;
  private final boolean useSsl;
  private final String from;
  private final String to;

  @Inject
  public SmtpNotificationChannel(
      @Named("smtp.host") String host,
      @Named("smtp.username") String username,
      @Named("smtp.password") String password,
      @Named("smtp.port") String port,
      @Named("smtp.useSsl") String useSsl,
      @Named("notification.smtp.from") String from,
      @Named("notification.smtp.to") String to) {
    this.host = checkNotNullOrEmpty(host);
    this.username = checkNotNullOrEmpty(username);
    this.password = checkNotNullOrEmpty(password);
    this.port = Integer.valueOf(port);
    this.useSsl = Boolean.valueOf(useSsl);
    this.from = checkNotNullOrEmpty(from);
    this.to = checkNotNullOrEmpty(to);
  }

  @Override
  public void notifySystemEvent(SystemEvent systemEvent) throws Exception {
    LOGGER.info(String.format(
        "Sending system event email (SMTP) notification to [%s]. Subject: [%s]",
        to,
        systemEvent.getClassName()));
    createEmail(systemEvent.getClassName(), systemEvent.getMessage()).send();
  }

  @Override
  public void notifyAlertEvent(AlertEvent alertEvent) throws Exception {
    String title = String.format(
        "[%s] alert, severity: [%s]",
        alertEvent.getPriceTick().getInstrumentId(),
        alertEvent.getSeverityLevel());
    String message = alertEvent.getMessage();
    LOGGER.info(
        String.format("Sending alert event email (SMTP) to [%s]. Subject: [%s]", to, title));
    createEmail(title, message).send();
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
