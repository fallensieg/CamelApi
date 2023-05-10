package uk.co.joshjordan.camel.routes;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.hl7.fhir.r4.model.Request;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.joshjordan.camel.beans.CreateReferralBean;
import uk.co.joshjordan.camel.entities.Referral;

import java.util.ArrayList;

@Component
public class ReferralServices  extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ReferralServices.class);

    private ArrayList<Referral> referralArrayList = new ArrayList<Referral>();

    @Autowired
    private CreateReferralBean createReferralBean;

    @Override
    public void configure() throws Exception {

        from("direct:createReferral")
                .routeId("createReferral")
                .to("direct:setReferralHeaders")
                .bean(CreateReferralBean.class, "AddToArrayList('referralArrayList')")
                .log(LoggingLevel.INFO, logger, "Referral has been stored");


        from("direct:setReferralHeaders")
                .convertBodyTo(String.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        String serviceRequestString = exchange.getIn().getBody(String.class);

                        FhirContext fhirContext = FhirContext.forR4();
                        IParser parser = fhirContext.newJsonParser();
                        ServiceRequest request = parser.parseResource(ServiceRequest.class, serviceRequestString);

                        exchange.getMessage().setHeader("patientId", request.getIdentifier().get(0).getValue());
                        exchange.getMessage().setHeader("PatientIdOid", request.getIdentifier().get(0).getSystem());
                        exchange.getMessage().setHeader("ReferralId", request.getId());
                        exchange.getMessage().setHeader("Status", request.getStatus());
                    }
                })
                .log(LoggingLevel.INFO, logger, "Headers have been set")
        .end();

        .from("direct:getReferral")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String idRequested = exchange.getIn().getHeader("id", String.class);
                        logger.info("Getting ServiceRequest With Id: " + idRequested);

                        Request
                    }
                })

    }
}


