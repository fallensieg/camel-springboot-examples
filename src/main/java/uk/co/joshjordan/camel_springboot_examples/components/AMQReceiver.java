package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AMQReceiver extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("activemq:queue:q-player")
                .routeId("AMQReceiverId")
                .log(LoggingLevel.INFO, "Message Received: ${body}");
    }
}
