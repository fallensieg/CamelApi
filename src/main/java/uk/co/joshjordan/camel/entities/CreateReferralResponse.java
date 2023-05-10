package uk.co.joshjordan.camel.entities;

public class CreateReferralResponse {

    private String referralId;

    private String patientId;

    private String patientIdOid;
    private String message;

    public CreateReferralResponse(){

    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientIdOid() {
        return patientIdOid;
    }

    public void setPatientIdOid(String patientIdOid) {
        this.patientIdOid = patientIdOid;
    }
}
