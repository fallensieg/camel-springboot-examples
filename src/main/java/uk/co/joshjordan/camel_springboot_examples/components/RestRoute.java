package uk.co.joshjordan.camel_springboot_examples.components;

import jakarta.jms.JMSException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel_springboot_examples.beans.Player;
import uk.co.joshjordan.camel_springboot_examples.beans.RestProcessingBean;
import uk.co.joshjordan.camel_springboot_examples.processor.InboundMessageProcessor;

import java.net.ConnectException;

@Component
public class RestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
/*
 curl --location 'http://localhost:8081/myRestEndpoint/player' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Josh",
    "club": "Cardiff",
    "position": "GK",
    "handed": "R"
}'
* */
Predicate isPositionGK = header("position").isEqualTo("GK");

       onException(JMSException.class, ConnectException.class, ExchangeTimedOutException.class)
                .routeId("JMS Exception Handler")
                .handled(true)
                .log(LoggingLevel.INFO, "JMS Exception has occurred while connecting to queue broker.");

        restConfiguration()
                .component("servlet")
                .host("0.0.0.0")
                .port(8081)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true);

        rest("myRestEndpoint")
                .produces("application/json")
                .post("player")
                .type(Player.class)
                .routeId("restProcessPlayer")
                .to("direct:processPlayer");

        from("direct:processPlayer")
                .routeId("processPlayerId")
                .log(LoggingLevel.INFO, "${body}")
                .bean(new RestProcessingBean())
                .to("direct:persistMessage")
                .wireTap("seda:activeMqPublish")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform().simple("Message has been processed. ${body}")
                .end();

        from("direct:persistMessage")
                .routeId("persistMessageId")
                .to("jpa:"+Player.class.getName());

        from("seda:activeMqPublish")
                .routeId("activeMqPublish")
                .to("activemq:queue:q-player?exchangePattern=InOnly"); //exchangePattern=InOnly means we dont expect a response
    }
}
