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
                .log(LoggingLevel.INFO, "JMS Exception has occurred.");

        restConfiguration()
                .component("jetty")
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
                .log(LoggingLevel.INFO, "${body}")
/*                .process(new InboundMessageProcessor())
                .log(LoggingLevel.INFO, "Transformed Body: ${body}")
                .convertBodyTo(String.class)
                .to("file:src/data/fileRoute/output?fileName=restoutput.csv&fileExist=append&appendChars=\\n");*/
                //.multicast() //send to both endpoints without waiting on each other
                .bean(new RestProcessingBean())
                .choice()
                .when(isPositionGK)
                    .log(LoggingLevel.INFO, "GK found")
                    .to("direct:toActiveMq")
                .otherwise()
                    .log(LoggingLevel.INFO, "Sending to toDb endpoint")
                    .to("direct:toDb")
                    .log(LoggingLevel.INFO, "Sending to toActiveMq endpoint")
                    .to("direct:toActiveMq")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform().simple("Message has been processed. ${body}")
                .end();




        from("direct:toDb")
                .routeId("toDbId")
                .log(LoggingLevel.INFO, "Inside toDb endpoint")
                .to("jpa:"+Player.class.getName());

        from("direct:toActiveMq")
                .routeId("toActiveMq")
                .log(LoggingLevel.INFO, "Inside toActiveMq endpoint")
                .to("activemq:queue:q-player?exchangePattern=InOnly"); //exchangePattern=InOnly means we dont expect a response
    }
}
