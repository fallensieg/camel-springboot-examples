package uk.co.joshjordan.camel_springboot_examples.beans;

import org.apache.camel.Exchange;

public class RestProcessingBean {

    public void validate(Exchange _exchange){
        Player player = _exchange.getIn().getBody(Player.class);
        _exchange.getIn().setHeader("position", player.getPosition());
    }
}
