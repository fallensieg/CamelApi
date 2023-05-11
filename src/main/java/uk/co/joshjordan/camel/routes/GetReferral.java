package uk.co.joshjordan.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel.entities.GetReferralResponse;
import uk.co.joshjordan.camel.entities.Referral;

import java.util.ArrayList;


@Component
public class GetReferral extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GetReferral.class);

    @Override
    public void configure() throws Exception {

        from("direct:getReferral")
                .routeId("getReferral")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String idRequested = exchange.getIn().getHeader("referralID", String.class);
                        logger.info("Getting ServiceRequest With Id: " + idRequested);

                        GetReferralResponse getReferralResponse = new GetReferralResponse();
                        ArrayList<Referral> referralArrayList  = Api.getReferralArrayList();
                        logger.info("ArraySize: " + referralArrayList.size());
                        for(int x= 0; x < referralArrayList.size(); x++){
                            logger.info("Checking Referral: " + referralArrayList.get(x).getReferralId());
                            if(referralArrayList.get(x).getReferralId().equals(idRequested)){

                                Referral referral = new Referral(
                                        referralArrayList.get(x).getReferralId()
                                        ,referralArrayList.get(x).getPatientId()
                                        ,referralArrayList.get(x).getPatientIdOid()
                                        ,referralArrayList.get(x).getStatus()
                                        ,null
                                );
                                getReferralResponse.setReferral(referral);
                                getReferralResponse.setMessage("Referral Found");
                            }
                            else{
                                getReferralResponse.setMessage("No referral found for id: " + idRequested);
                            }
                        }
                        exchange.getMessage().setBody(getReferralResponse);
                    }
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .marshal().json(JsonLibrary.Jackson)
                .end();
    }
}