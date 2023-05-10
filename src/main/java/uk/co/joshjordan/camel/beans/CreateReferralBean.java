package uk.co.joshjordan.camel.beans;

import org.springframework.stereotype.Component;
import uk.co.joshjordan.camel.entities.Referral;

import java.util.ArrayList;

@Component
public class CreateReferralBean {
    public ArrayList<Referral> AddToArrayList(ArrayList<Referral> referralArrayList){
        Referral referral = new Referral();
        referral.setPatientId("");
        referral.setPatientIdOid("");
        referral.setReferralId("");
        referral.setStatus("");
        referral.setServiceRequest(null);

        referralArrayList.add(referral);

        return referralArrayList;
    }
}
