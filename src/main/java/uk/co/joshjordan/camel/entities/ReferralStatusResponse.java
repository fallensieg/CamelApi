package uk.co.joshjordan.camel.entities;


public class ReferralStatusResponse {

    private String PatientIdentifier;
    private String ReferralStatus;

    public ReferralStatusResponse(String patientIdentifier, String referralStatus){
        this.PatientIdentifier = patientIdentifier;
        this.ReferralStatus = referralStatus;
    }

    public String getPatientIdentifier() {
        return PatientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        PatientIdentifier = patientIdentifier;
    }

    public String getReferralStatus() {
        return ReferralStatus;
    }

    public void setReferralStatus(String referralStatus) {
        ReferralStatus = referralStatus;
    }
}
