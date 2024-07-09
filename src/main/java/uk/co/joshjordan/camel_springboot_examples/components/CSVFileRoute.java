package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.beanio.BeanIODataFormat;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel_springboot_examples.processor.InboundMessageProcessor;

@Component
public class CSVFileRoute extends RouteBuilder {

    //Logger _logger = LoggerFactory.getLogger(getClass());
    BeanIODataFormat inboundDataFormat = new BeanIODataFormat("PlayingInboundMessageBeanIOMapping.xml", "inputMessageStream");

    @Override
    public void configure() throws Exception {
        from("file:src/data/fileRoute/input?fileName=input.csv")
                .routeId("CSVFileRouteId")
                .split(body().tokenize("\n", 1, true)) //split on a new line, each row is its own "group", skip the header row on the first line
                .unmarshal(inboundDataFormat)//unmarshall to this object
                .process(new InboundMessageProcessor())
                .log(LoggingLevel.INFO, "Transformed Body: ${body}")
                /*  Example of in route processor
                .process(exchange -> {
                    Player fileData = exchange.getIn().getBody(Player.class); //Get the exchanges inbound message (i.e. whats in the file) and convert it to type String
                    logger.info("This are the row values: {}", fileData.toString());
                    //exchange.getIn().setBody(fileData.toString()); //Convert the java object back to a string
                })
                */

                .convertBodyTo(String.class) // same as the exchange.getIn line
                .to("file:src/data/fileRoute/output?fileName=output.csv&fileExist=append&appendChars=\\n") //As we've split on the new line char, its no longer part of the file. So we need to add it back in.
        .end();
    }
}
