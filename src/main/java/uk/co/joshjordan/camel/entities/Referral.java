package uk.co.joshjordan.camel.entities;

import org.hl7.fhir.r4.model.ServiceRequest;

public class Referral {

    private String ReferralId;
    private String PatientId;
    private String PatientIdOid;
    private String Status;

    private ServiceRequest serviceRequest;

    public String getReferralId() {
        return ReferralId;
    }

    public void setReferralId(String referralId) {
        ReferralId = referralId;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public String getPatientIdOid() {
        return PatientIdOid;
    }

    public void setPatientIdOid(String patientIdOid) {
        PatientIdOid = patientIdOid;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }
}
