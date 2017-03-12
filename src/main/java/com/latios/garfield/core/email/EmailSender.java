package com.latios.garfield.core.email;

import com.latios.garfield.GarfieldConfig;
import com.latios.garfield.GarfieldConsts;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 * @author zebin
 * @since 2016-10-05.
 */
public class EmailSender {
    private static final Logger LOG = Logger.getLogger(EmailSender.class);
    private static final GarfieldConfig config = GarfieldConfig.getInstance();
    private static final String user = config.get(GarfieldConsts.CONFIG_EMAIL_SERVER_USER);
    private static final String pass = config.get(GarfieldConsts.CONFIG_EMAIL_SERVER_PASS);
    private static final String host = config.get(GarfieldConsts.CONFIG_EMAIL_SERVER_HOST);
    private static final int port = config.getAsInt(GarfieldConsts.CONFIG_EMAIL_SERVER_PORT);

    public EmailSender() {
        LOG.info(String.format("user=%s, pass=%s, host=%s, port=%s", user, pass, host, port));
    }

    public void send(String title, String content, String sendTo) throws EmailException {
        SimpleEmail email = new SimpleEmail();
        email.setHostName(host);
        email.setSmtpPort(port);
        email.setFrom(user);
        email.setSSLOnConnect(true);
        email.setAuthentication(user, pass);
        email.addTo(sendTo);
        email.addCc(user);
        email.setSubject(title);
        email.setContent(content, "text/html; charset=utf-8");
        email.send();
    }
}
