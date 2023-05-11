package uk.co.joshjordan.camel.routes;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.joshjordan.camel.beans.CreateReferralBean;
import uk.co.joshjordan.camel.entities.CreateReferralResponse;
import uk.co.joshjordan.camel.entities.Referral;

@Component
public class CreateReferral extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CreateReferral.class);
    @Override
    public void configure() throws Exception {

        from("direct:createServiceRequest")
            .routeId("createReferral")
            //.bean(CreateReferralBean.class, "AddToArrayList('referralArrayList')")
            .log(LoggingLevel.INFO, logger, "Referral has been stored")
            .convertBodyTo(String.class)
            .process(new Processor() { //this should be moved to a separate bean
                @Override
                public void process(Exchange exchange) throws Exception {

                    String serviceRequestString = exchange.getIn().getBody(String.class);

                    FhirContext fhirContext = FhirContext.forR4();
                    IParser parser = fhirContext.newJsonParser();
                    ServiceRequest request = parser.parseResource(ServiceRequest.class, serviceRequestString);

                    String patientId = request.getIdentifier().get(0).getValue();
                    String patientIdOid = request.getIdentifier().get(0).getSystem();
                    String referralId = request.getId();
                    String referralStatus = request.getStatus().toString();

                    exchange.getMessage().setHeader("PatientId", patientId);
                    exchange.getMessage().setHeader("PatientIdOid", patientIdOid);
                    exchange.getMessage().setHeader("ReferralId", referralId);
                    exchange.getMessage().setHeader("Status", referralStatus);

                    Api.getReferralArrayList().add(new Referral(referralId,patientId,patientIdOid,referralStatus, request));
                    logger.info("Referral Stored: " + referralId);
                }
            })
            .log(LoggingLevel.INFO, logger, "Headers have been set")
            .process(new Processor() { //this should be moved to a separate bean
                @Override
                public void process(Exchange exchange) throws Exception {
                    CreateReferralResponse createReferralResponse = new CreateReferralResponse();
                    createReferralResponse.setReferralId(exchange.getMessage().getHeader("ReferralId", String.class));
                    createReferralResponse.setPatientId(exchange.getMessage().getHeader("PatientId", String.class));
                    createReferralResponse.setPatientIdOid(exchange.getMessage().getHeader("PatientIdOid", String.class));
                    createReferralResponse.setMessage("Referral has been saved.");
                    exchange.getMessage().setBody(createReferralResponse);
                }
            })
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
            .marshal().json(JsonLibrary.Jackson)
        .end();

    }

}
