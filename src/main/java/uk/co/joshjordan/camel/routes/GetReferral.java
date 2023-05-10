package uk.co.joshjordan.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.joshjordan.camel.entities.CreateReferralResponse;

public class GetReferral extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GetReferral.class);

    @Override
    public void configure() throws Exception {

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

        RestConfigurationDefinition restConfiguration = restConfiguration();
        restConfiguration.component("servlet")
                .bindingMode(RestBindingMode.off)
                .contextPath("/api")
                .port(8080)
                .enableCORS(true)
                .apiContextPath("/api-docs")
                .dataFormatProperty("prettyPrint", "true")
                .apiProperty("api.title", "My REST API")
                .apiProperty("api.version", "1.0");

        rest()

                .consumes("application/fhir+json")
                .produces("application/json")

                .post("/createReferral")
                .outType(CreateReferralResponse.class)
                .description("Create a new FHIR ServiceRequest Bundle")
                .param()
                .name("body")
                .type(RestParamType.body)
                .description("The FHIR ServiceRequest Bundle to create")
                .endParam()
                .to("direct:createServiceRequest")


                .get("/getReferral/{id}")
                .outType(ServiceRequest.class)
                .description("Create a new FHIR ServiceRequest Bundle")
                .to("direct:getReferral");


        from("direct:createServiceRequest")
                .to("direct:createReferral")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        CreateReferralResponse createReferralResponse = new CreateReferralResponse();
                        createReferralResponse.setReferralId(exchange.getMessage().getHeader("ReferralId", String.class));
                        createReferralResponse.setMessage("Referral has been saved.");
                        exchange.getMessage().setBody(createReferralResponse);
                    }
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .marshal().json(JsonLibrary.Jackson)
                .end();

        from("direct:getReferral")
                .to("direct:getReferral");
    }
}