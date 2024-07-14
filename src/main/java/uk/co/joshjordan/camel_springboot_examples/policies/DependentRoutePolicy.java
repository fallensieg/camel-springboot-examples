package uk.co.joshjordan.camel_springboot_examples.policies;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;

public class DependentRoutePolicy extends RoutePolicySupport {
    private String routeName1;
    private String routeName2;

    public DependentRoutePolicy(String _routeName1, String _routeName2) {
        this.routeName1 = _routeName1;
        this.routeName2 = _routeName2;
    }

/*    @Override
    public void onStart(Route _route) {
        CamelContext camelContext = _route.getCamelContext();
        try{
            camelContext.getRouteController().startRoute(routeName1);
            camelContext.getRouteController().startRoute(routeName2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Override
    public void onStop(Route _route) {
        CamelContext camelContext = _route.getCamelContext();
        String routeToStart = _route.getRouteId().equals(routeName1) ? routeName2 : routeName1;
        String routeToStop = _route.getRouteId().equals(routeName1) ? routeName1 : routeName2;

        try{
            camelContext.getRouteController().stopRoute(routeToStop);
            camelContext.getRouteController().startRoute(routeToStart);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
