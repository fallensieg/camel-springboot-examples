package uk.co.joshjordan.camel_springboot_examples.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.policy.HazelcastRoutePolicy;
import org.apache.camel.spi.RoutePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel_springboot_examples.beans.Player;
import uk.co.joshjordan.camel_springboot_examples.policies.DependentRoutePolicy;
import uk.co.joshjordan.camel_springboot_examples.processor.InboundMessageProcessor;

@Component
public class BatchProcessingRoute extends RouteBuilder {

    @Autowired
    HazelcastRoutePolicy hazelcastRoutePolicy;

    @Override
    public void configure() throws Exception {

        //RoutePolicy dependRoutePolicy = new DependentRoutePolicy("BatchProcessingRouteId", "AMQReceiverId");

        from ("timer:readDB?period=5000")
                .routeId("BatchProcessingRouteId")
                //.routePolicy(dependRoutePolicy)
                //.routePolicy(hazelcastRoutePolicy)
                .to("jpa:"+Player.class.getName()+"?namedQuery=getAllRows")
                .split(body())
                .process(new InboundMessageProcessor())
                .log(LoggingLevel.INFO, "Returned Body: ${body}")
                .convertBodyTo(String.class) // same as the exchange.getIn line
                .to("file:src/data/fileRoute/output?fileName=sqlOutput.csv&fileExist=append&appendChars=\\n")
                .toD("jpa:"+Player.class.getName()+"?nativeQuery=DELETE FROM PLAYER WHERE id = ${header.consumedId}&useExecuteUpdate=true") //toD because the values are dynamic (consumedId)
                .end();

/*        from("timer:stopBatchRoute?repeatCount=1&delay=5000")
                .routeId("stopBatchRouteId")
                .to("controlbus:route?routeId=BatchProcessingRouteId&action=stop")
                .to("controlbus:route?routeId=BatchProcessingRouteId&action=status")
                .to("controlbus:route?routeId=AMQReceiverId&action=status");

        from("timer:startBatchRoute?repeatCount=1&delay=15000")
                .routeId("stopAMQReceiverId")
                .to("controlbus:route?routeId=AMQReceiverId&action=stop")
                .to("controlbus:route?routeId=BatchProcessingRouteId&action=status")
                .to("controlbus:route?routeId=AMQReceiverId&action=status");*/
    }
}
