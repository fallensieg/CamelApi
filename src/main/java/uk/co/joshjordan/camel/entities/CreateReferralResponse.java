package uk.co.joshjordan.camel.entities;

public class CreateReferralResponse {

    private int referralId;
    private String status;

    public int getReferralId() {
        return referralId;
    }

    public void setReferralId(int referralId) {
        this.referralId = referralId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
