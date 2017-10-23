package com.heartgo.model;

import org.hibernate.validator.constraints.NotBlank;

public class TransactionCriteria {
    @NotBlank(message = "organizationid can't empty!")

    String organizationid;
    public String getOrganizationid() {
        return organizationid;
    }
    public void setOrganizationid(String organizationid) {
        this.organizationid = organizationid;
    }
}
