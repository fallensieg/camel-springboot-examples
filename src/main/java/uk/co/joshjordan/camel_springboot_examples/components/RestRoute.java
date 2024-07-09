package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel_springboot_examples.beans.Player;
import uk.co.joshjordan.camel_springboot_examples.processor.InboundMessageProcessor;

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
                .to("jpa:"+Player.class.getName());
    }
}
