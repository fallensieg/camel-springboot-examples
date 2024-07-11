package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel_springboot_examples.beans.Player;
import uk.co.joshjordan.camel_springboot_examples.processor.InboundMessageProcessor;

@Component
public class BatchProcessingRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from ("timer:readDB?period=5000")
                .routeId("BatchProcessingRouteId")
                .to("jpa:"+Player.class.getName()+"?namedQuery=getAllRows")
                .split(body())
                .process(new InboundMessageProcessor())
                .log(LoggingLevel.INFO, "Returned Body: ${body}")
                .convertBodyTo(String.class) // same as the exchange.getIn line
                .to("file:src/data/fileRoute/output?fileName=sqlOutput.csv&fileExist=append&appendChars=\\n")
                .toD("jpa:"+Player.class.getName()+"?nativeQuery=DELETE FROM PLAYER WHERE id = ${header.consumedId}&useExecuteUpdate=true") //toD because the values are dynamic (consumedId)
                .end();
    }
}
