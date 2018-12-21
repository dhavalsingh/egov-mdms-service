package org.egov.wf.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.egov.common.contract.request.User;

import java.util.List;

@Data
public class ProcessInstanceSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("status")
    private List<String> status;

    @JsonProperty("businessId")
    private String businessId;

    @JsonProperty("assignee")
    private String  assignee;

    @JsonProperty("ids")
    private List<String> ids;

    @JsonProperty("history")
    private Boolean history = false;

    @JsonProperty("fromDate")
    private Long fromDate = null;

    @JsonProperty("toDate")
    private Long toDate = null;


    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;


    public Boolean isNull(){
        if(this.getBusinessId()==null && this.getIds()==null && this.getAssignee()==null &&
                this.getStatus()==null)
            return true;
        else return false;
    }



}