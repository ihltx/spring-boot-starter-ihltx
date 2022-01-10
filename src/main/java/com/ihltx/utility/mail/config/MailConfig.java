package com.ihltx.utility.mail.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "ihltx.mail")
public class MailConfig {

    /**
     *  Specify mail delivery protocol,for exampl: smtp
     */
    private String protocol;

    /**
     *  Specify the host to use for sending mail
     */
    private String host;

    /**
     * Specify the port to use for sending mail
     */
    private Integer port;

    /**
     *  Specify the username to use for sending mail
     */
    private String username;

    /**
     * Specify the password to use for sending mail
     */
    private String password;

    /**
     * Specify the default encoding to use for sending mail
     */
    private String defaultEncoding ="utf-8";

    /**
     *  Specify the email address of the sender
     */
    private String from;

    /**
     * Specify the name of the sender
     */
    private String fromName;

    /**
     * Specify additional configuration information for mail sending
     *  for example:
     *        //Specifies whether mail sending authentication is enabled
     *       "mail.smtp.auth": true
     *       //Specifies whether TLS is enabled for mail sending
     *       "mail.smtp.starttls.enable": true
     *       //Specifies whether TLS is required for mail sending
     *       "mail.smtp.starttls.required": true
     *       //Specifies the fully qualified class name used by the mail sending socket factory
     *       "mail.smtp.socketFactory.class": "javax.net.ssl.SSLSocketFactory"
     *       //Specifies the port used by the mail sending socket factory
     *       "mail.smtp.socketFactory.port ": 465
     *       //Specifies whether the mail sending socket factory uses fallback
     *       "mail.smtp.socketFactory.fallback": false
     *       //Specifies whether SSL is enabled for mail sending
     *       "mail.smtp.ssl.enable": true
     */

    private Map<String,String> properties;


}


