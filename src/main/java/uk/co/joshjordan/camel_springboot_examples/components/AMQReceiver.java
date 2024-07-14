package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.RoutePolicy;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel_springboot_examples.policies.DependentRoutePolicy;
import uk.co.joshjordan.camel_springboot_examples.processor.RuleProcessor;

@Component
public class AMQReceiver extends RouteBuilder {

    RoutePolicy dependRoutePolicy = new DependentRoutePolicy("BatchProcessingRouteId", "AMQReceiverId");

    @Override
    public void configure() throws Exception {
        from("activemq:queue:q-player")
                .routeId("AMQReceiverId")
               // .routePolicy(dependRoutePolicy)
                .process(new RuleProcessor())
                .log(LoggingLevel.INFO, "Message Received: ${body}");
    }
}
