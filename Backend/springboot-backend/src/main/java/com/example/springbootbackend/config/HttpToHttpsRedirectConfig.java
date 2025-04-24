package com.example.springbootbackend.config;

import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> redirectHttpToHttps() {
        return factory -> ((TomcatServletWebServerFactory) factory).addAdditionalTomcatConnectors(httpConnector());
    }

    private org.apache.catalina.connector.Connector httpConnector() {
        org.apache.catalina.connector.Connector connector = new org.apache.catalina.connector.Connector(
                org.apache.coyote.http11.Http11NioProtocol.class.getName());
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
