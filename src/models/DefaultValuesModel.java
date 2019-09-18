package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultValuesModel {

    @JsonProperty("Planecores")
    private String planecores;

    @JsonProperty("csfFailureDetectionTimer")
    private String csfFailureDetectionTimer;

    @JsonProperty("licenseFile")
    private String licenseFile;

    @JsonProperty("primaryConfigFile")
    private String primaryConfigFile;

    @JsonProperty("zoneId1")
    private String zoneId1;

    @JsonProperty("zoneId2")
    private String zoneId2;
    
    @JsonProperty("controlSwitchFabricCidr")
    private String controlSwitchFabricCidr;
    
    @JsonProperty("mgLbManagementCidr")
    private String mgLbManagementCidr;
    
    
    
    @JsonProperty("Planecores")
    public String getPlanecores() {
        return planecores;
    }

    @JsonProperty("Planecores")
    public void setPlanecores(String planecores) {
        this.planecores = planecores;
    }

    @JsonProperty("csfFailureDetectionTimer")
    public String getCsfFailureDetectionTimer() {
        return csfFailureDetectionTimer;
    }

    @JsonProperty("csfFailureDetectionTimer")
    public void setCsfFailureDetectionTimer(String csfFailureDetectionTimer) {
        this.csfFailureDetectionTimer = csfFailureDetectionTimer;
    }

    @JsonProperty("licenseFile")
    public String getLicenseFile() {
        return licenseFile;
    }

    @JsonProperty("licenseFile")
    public void setLicenseFile(String licenseFile) {
        this.licenseFile = licenseFile;
    }

    @JsonProperty("primaryConfigFile")
    public String getPrimaryConfigFile() {
        return primaryConfigFile;
    }

    @JsonProperty("primaryConfigFile")
    public void setPrimaryConfigFile(String primaryConfigFile) {
        this.primaryConfigFile = primaryConfigFile;
    }

    @JsonProperty("zoneId1")
    public String getZoneId1() {
        return zoneId1;
    }

    @JsonProperty("zoneId1")
    public void setZoneId1(String zoneId1) {
        this.zoneId1 = zoneId1;
    }

    @JsonProperty("zoneId2")
    public String getZoneId2() {
        return zoneId2;
    }

    @JsonProperty("zoneId2")
    public void setZoneId2(String zoneId2) {
        this.zoneId2 = zoneId2;
    }

    @JsonProperty("controlSwitchFabricCidr")
    public String getControlSwitchFabricCidr() {
        return controlSwitchFabricCidr;
    }

    @JsonProperty("controlSwitchFabricCidr")
    public void setControlSwitchFabricCidr(String controlSwitchFabricCidr) {
        this.controlSwitchFabricCidr = controlSwitchFabricCidr;
    }

    @JsonProperty("mgLbManagementCidr")
    public String getMgLbManagementCidr() {
        return mgLbManagementCidr;
    }

    @JsonProperty("mgLbManagementCidr")
    public void setMgLbManagementCidr(String mgLbManagementCidr) {
        this.mgLbManagementCidr = mgLbManagementCidr;
    }
}