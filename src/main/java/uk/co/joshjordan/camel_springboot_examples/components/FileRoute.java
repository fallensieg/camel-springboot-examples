package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void configure() throws Exception {
        from("file:src/data/fileRoute/input?fileName=input.txt")
                .routeId("FileRouteId")
                .log(LoggingLevel.INFO, "File has been found, moving it!")
                .process(exchange -> {
                    //Inside the process we can execute Java code
                    logger.info("I am now inside the process");
                    String fileData = exchange.getIn().getBody(String.class); //Get the exchanges inbound message (i.e. whats in the file) and convert it to type String
                    logger.info("This is the file data: {}", fileData);
                })
                .to("file:src/data/fileRoute/output?fileName=output.txt");
    }
}
