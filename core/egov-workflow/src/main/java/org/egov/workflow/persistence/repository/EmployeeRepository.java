package org.egov.workflow.persistence.repository;

import org.egov.common.contract.request.RequestInfo;
import org.egov.workflow.web.contract.Employee;
import org.egov.workflow.web.contract.EmployeeRes;
import org.egov.workflow.web.contract.RequestInfoWrapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmployeeRepository {

    private final RestTemplate restTemplate;
    private final String employeesByUserIdUrl;
    private final String employeesByPositionIdurl;

    private final String employeesByRoleCodeurl;

    @Autowired
    public EmployeeRepository(final RestTemplate restTemplate,
            @Value("${egov.services.hremployee.host}") final String hrEmployeeServiceHostname,
            @Value("${egov.services.hr.employee_by_userid}") final String hrEmployeesByUserIdUrl,
            @Value("${egov.services.hr.employee_by_position}") final String hrEmployeesByPositionIdurl,
            @Value("${egov.services.hr_employee_by_role}") final String hrEmployeesByRoleCodeurl) {

        this.restTemplate = restTemplate;
        this.employeesByUserIdUrl = hrEmployeeServiceHostname + hrEmployeesByUserIdUrl;
        this.employeesByPositionIdurl = hrEmployeeServiceHostname + hrEmployeesByPositionIdurl;
        this.employeesByRoleCodeurl = hrEmployeeServiceHostname + hrEmployeesByRoleCodeurl;
        //this.employeesByRoleCodeurl =  hrEmployeesByRoleCodeurl;
    }

    public List<Employee> getByRoleCode(final String roleCode, final String tenantId) {
        RequestInfoWrapper wrapper = RequestInfoWrapper.builder().RequestInfo(RequestInfo.builder().build()).build();
        final EmployeeRes employeeRes = restTemplate.postForObject(employeesByRoleCodeurl,wrapper,EmployeeRes.class,roleCode,tenantId);
        return employeeRes.getEmployees();
    }

    public EmployeeRes getEmployeeForPositionAndTenantId(final Long posId, final LocalDate asOnDate, final String tenantId) {
        RequestInfoWrapper wrapper = RequestInfoWrapper.builder().RequestInfo(RequestInfo.builder().build()).build();
        return restTemplate.postForObject(employeesByPositionIdurl,wrapper,EmployeeRes.class, tenantId, posId, asOnDate);
    }

    public EmployeeRes getEmployeeForUserIdAndTenantId(final Long userId,final String tenantId) {
        RequestInfoWrapper wrapper = RequestInfoWrapper.builder().RequestInfo(RequestInfo.builder().build()).build();
       return restTemplate.postForObject(employeesByUserIdUrl,wrapper,EmployeeRes.class, userId, tenantId);
    }
}
