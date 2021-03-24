package com.example.bookclub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${mail.smtp.host")
    private String host;
    @Value("${mail.smtp.port}")
    private int port;
    @Value("${mail.smtp.socketFactory.port}")
    private int socketPort;
    @Value("${mail.smtp.auth}")
    private boolean isAuth;
    @Value("${mail.smtp.starttls.enable}")
    private boolean isStarttls;
    @Value("${mail.smtp.starttls.required}")
    private boolean isStarttlsRequired;
    @Value("${mail.smtp.socketFactory.fallback}")
    private boolean isFallback;
    @Value("${mail.id}")
    private String id;
    @Value("${mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(id);
        javaMailSender.setPassword(password);
        javaMailSender.setJavaMailProperties(getMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.socketFactory.port", socketPort);
        properties.put("mail.smtp.auth", isAuth);
        properties.put("mail.smtp.starttls.enable", isStarttls);
        properties.put("mail.smtp.starttls.required", isStarttlsRequired);
        properties.put("mail.smtp.socketFactory.fallback",isFallback);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return properties;
    }
}
