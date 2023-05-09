package uk.co.joshjordan.camel.routes;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel.entities.CreateReferralResponse;
import uk.co.joshjordan.camel.entities.ReferralStatusRequest;
import uk.co.joshjordan.camel.entities.ReferralStatusResponse;


public class Api extends RouteBuilder {

    // Logger component
    private static final Logger logger = LoggerFactory.getLogger(Api.class);

    @Override
    public void configure() throws Exception {


        restConfiguration()
                .component("servlet")
                .port(9090)
                .host("localhost")
                .bindingMode(RestBindingMode.auto);

        onException()
                .log("### Exception Caught ###")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .log("${exception.stacktrace}")
                .to("log:logger?showAll=true")
                .setBody(simple("Message: An Error Has Occurred! ${exception.message}"))
                .handled(true);

        onException(org.apache.camel.component.bean.MethodNotFoundException.class)
                .log("### Exception Caught ###")
                .to("log:logger?showAll=true")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                //.log("${exception.stacktrace}")
                .setBody(simple("Error Response: ${exception.message}"))
                .handled(true);

        rest()

            .get("/getReferralStatus")
                .type(ReferralStatusRequest.class)
                .outType(ReferralStatusResponse.class)
                .to("direct:getReferralStatus")

            .post("/createReferral")
                .type(ServiceRequest.class)
                            .outType(CreateReferralResponse.class)
                                    .to("direct:createReferral");

        from("direct:checkConnectivity")
                .log("checkConnectivity route running")
                .process(this::getConnectivityCheck);

        from("direct:getReferralStatus")
                .log("getReferralStatus route running")
                .log("Processing PatientId: ${body.patientId}")

                .to("log:logger?showAll=true")
                .process(this::getReferralStatus);

        from("direct:createReferral")
                .routeId("RouteId - FHIRBundleProcessor")
                .to("log:logger?showAll=true")

                .process(exchange ->  {
                    FhirContext fhirContext = FhirContext.forR4();
                    IParser parser = fhirContext.newJsonParser();
                    ServiceRequest serviceRequest = parser.parseResource(ServiceRequest.class, exchange.getIn().getBody(String.class));

                    String id = serviceRequest.getIdElement().getId();
                    System.out.println("ServiceRequest ID: " + id);
                })
                .log("Completed parsing.  Persist to ....");

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

    private void createReferral(Exchange exchange){
        ServiceRequest request = exchange.getMessage().getBody(ServiceRequest.class);
        System.out.println("Request Id: " + request.getId());
    }
}
