package uk.co.joshjordan.camel.entities;

public class GetReferralResponse {
    private String Message;
    private Referral Referral;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String referralId) {
        Message = referralId;
    }

    public uk.co.joshjordan.camel.entities.Referral getReferral() {
        return Referral;
    }

    public void setReferral(uk.co.joshjordan.camel.entities.Referral referral) {
        Referral = referral;
    }
}
