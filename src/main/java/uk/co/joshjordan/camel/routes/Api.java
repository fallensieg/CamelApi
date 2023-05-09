package uk.co.joshjordan.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel.entities.ReferralStatusRequest;
import uk.co.joshjordan.camel.entities.ReferralStatusResponse;

@Component
public class Api extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .port(9090)
                .host("localhost")
                .bindingMode(RestBindingMode.auto);

        rest()
                .get("/checkConnectivity").to("direct:checkConnectivity")

                .get("/getReferralStatus")
                .type(ReferralStatusRequest.class)
                .outType(ReferralStatusResponse.class)
                .to("direct:getReferralStatus");

        from("direct:checkConnectivity")
                .log("checkConnectivity route running")
                .process(this::getConnectivityCheck);

        from("direct:getReferralStatus")
                .log("getReferralStatus route running")
                .log("Processing PatientId: ${body.patientId}")
                .to("log:logger?showAll=true")
                .process(this::getReferralStatus);

    }

    private void getConnectivityCheck(Exchange exchange){
        String yourName = exchange.getMessage().getHeader("name", String.class);
        if(yourName == null){
            exchange.getMessage().setBody("Hello. You've made a connection but you didn't provide a \"name\" param :(");
        }else{
            exchange.getMessage().setBody("Hello " + yourName + ". You've made a connection.");
        }
    }

    private void getReferralStatus(Exchange exchange){

        ReferralStatusRequest request = exchange.getMessage().getBody(ReferralStatusRequest.class);
        String referralStatus = retrieveStatusFromPatientId(request.getPatientId());

        ReferralStatusResponse referralStatusResponse = new ReferralStatusResponse(request.getPatientId(), referralStatus);
        exchange.getMessage().setBody(referralStatusResponse);
    }

    private String retrieveStatusFromPatientId(String patientId){
        String lastChar = patientId.substring(patientId.length() - 1);

        switch(lastChar){
            case "1":
                return "draft";

            case "2":
                return "active";

            case "3":
                return "on-hold";

            case "4":
                return "revoked";

            case "5":
                return "completed";

            case "6":
                return "enetered-in-error";

            case "7":
                return "unknown";

            default:
                return "draft";
        }
    }
}
