package uk.co.joshjordan.camel_springboot_examples.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.joshjordan.camel_springboot_examples.beans.OutboundPlayer;
import uk.co.joshjordan.camel_springboot_examples.beans.Player;

public class InboundMessageProcessor implements Processor {

    Logger _logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(Exchange exchange) throws Exception {
        Player player = exchange.getIn().getBody(Player.class); //Get the exchanges inbound message (i.e. whats in the file) and convert it to type String
        _logger.info("This is the row values: {}", player.toString());
        exchange.getIn().setBody(new OutboundPlayer(player.getName(), returnOutboundPlayerClubAndPosition(player)));
        exchange.getIn().setHeader("consumedId", player.getId());

    }

    private String returnOutboundPlayerClubAndPosition(Player player){
        StringBuilder concatClubAndPosition = new StringBuilder(100);
        concatClubAndPosition.append(player.getClub());
        concatClubAndPosition.append(" " + player.getPosition());

        return concatClubAndPosition.toString();
    }
}
