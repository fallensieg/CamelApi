package uk.co.joshjordan.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel.entities.CreateReferralResponse;
import uk.co.joshjordan.camel.entities.Referral;

import java.util.ArrayList;

@Component
public class Api extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Api.class);

    private static ArrayList<Referral> referralArrayList = new ArrayList<Referral>();

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
                //.contextPath("/api")
                .port(9090);
                //.enableCORS(true);
                //.apiContextPath("/api-docs")
                //.dataFormatProperty("prettyPrint", "true")
                //.apiProperty("api.title", "My REST API")
                //.apiProperty("api.version", "1.0");


        rest()

                .consumes("application/json")
                .produces("application/json")

                .post("/createReferral")
                    .outType(CreateReferralResponse.class)
                    .description("Create a new referral by sending in a ServiceRequest payload.")
                .to("direct:createServiceRequest")


                .get("/getReferral")
                    .outType(ServiceRequest.class)
                    .description("Return a referral based on the id provided.")
                    .param()
                        .name("referralId")
                        .type(RestParamType.query)
                        .description("The FHIR ServiceRequest Bundle to create")
                    .endParam()
                .to("direct:getReferral");



    }

    public static ArrayList<Referral> getReferralArrayList() {
        return referralArrayList;
    }
}
