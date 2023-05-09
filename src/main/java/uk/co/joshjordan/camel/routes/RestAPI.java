package uk.co.joshjordan.camel.routes;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component
public class RestAPI  extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RestAPI.class);
    @Override
    public void configure() throws Exception {

        RestConfigurationDefinition restConfiguration = restConfiguration();
        restConfiguration.component("servlet")
                .bindingMode(RestBindingMode.auto)
                .contextPath("/api")
                .port(8080)
                .enableCORS(true)
                .apiContextPath("/api-docs")
                .dataFormatProperty("prettyPrint", "true")
                .apiProperty("api.title", "My REST API")
                .apiProperty("api.version", "1.0");

        rest( )
                .post("/createReferral")
                .description("Create a new FHIR ServiceRequest Bundle")
                .consumes("application/fhir+json")
                .param().name("body").type(RestParamType.body).description("The FHIR ServiceRequest Bundle to create").endParam()
                .to("direct:createServiceRequest");

        from("direct:createServiceRequest")
                .log("Received FHIR ServiceRequest Bundle: ${body}")
                .process(this::processServiceRequest)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
    }

    private void processServiceRequest(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);

        FhirContext fhirContext = FhirContext.forR4();
        IParser parser = fhirContext.newJsonParser();
        ServiceRequest bundle = parser.parseResource(ServiceRequest.class, body);

        logger.info("Bundle stuff: " +bundle.getId());
    }
}


