package uk.co.joshjordan.camel_springboot_examples.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import uk.co.joshjordan.camel_springboot_examples.beans.Player;

public class RuleProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Player player = (Player) exchange.getIn().getBody();

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieContainer.newKieSession("ksession-rule");
        kieSession.insert(player);
        kieSession.fireAllRules();
        kieSession.destroy();

        if(null == player.getPreferred())
            System.out.println("Throw exception");
    }
}
