package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BasicTimer extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //component type : component name ? arguments
        from("timer:basicTimer?period=5000")
                .routeId("basicTimerRouteId") //Good practice to have a unique route id
                .setBody(constant("Hello Camel")) // Sets the body of the payload to Hello World
                .log(LoggingLevel.INFO, "${body}"); //Prints the body to the log, with the routeId name
    }
}
