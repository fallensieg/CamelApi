package uk.co.joshjordan.camel.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActivityCheck extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:ActivityCheck?period=10s")
                .setBody(simple("{{camel.springboot.name}} is online."))
                .log(LoggingLevel.INFO, org.slf4j.LoggerFactory.getLogger(ActivityCheck.class.getName()), "${body}")
                .end();
    }
}
