/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products AS by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License AS published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways AS different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.eis.repository.builder;

import lombok.extern.slf4j.Slf4j;
import org.egov.eis.config.ApplicationProperties;
import org.egov.eis.web.contract.BaseRegisterReportRequest;
import org.egov.eis.web.contract.EmployeeCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
public class EmployeeQueryBuilder {

    /**
     * FIXME : The sorting logic will not work apart from order by id or fromDate because we can't use orderBy
     * on the non-selected columns with DISTINCT, which will be problematic in any sort condition.
     * Also, we should not use the sorting logic in 2nd query, because then we may not get the desired result.
     * As it'll then sort the limited result having ids generated by this query. Rather than sorting the actual
     * data & limiting the records.
     * So, find a better solution for this.
     */
    private static final String EMPLOYEE_IDS_QUERY = "SELECT DISTINCT e.id AS id"
            + " FROM egeis_employee e"
            + " JOIN egeis_assignment a ON e.id = a.employeeId AND a.tenantId = e.tenantId"
            + " LEFT JOIN egeis_hodDepartment hod ON a.id = hod.assignmentId AND hod.tenantId = e.tenantId"
            + " LEFT JOIN egeis_employeeJurisdictions ej ON e.id = ej.employeeId AND ej.tenantId = e.tenantId"
            + " WHERE e.tenantId = :tenantId";
    private static final String BASE_QUERY = "SELECT e.id AS e_id, e.code AS e_code,"
            + " e.employeeStatus AS e_employeeStatus, e.dateOfAppointment as e_dateOfAppointment, e.employeeTypeId AS e_employeeTypeId, e.bankId AS e_bankId,"
            + " e.bankBranchId AS e_bankBranchId, e.dateOfRetirement as e_dateOfRetirement, e.bankAccount AS e_bankAccount, e.tenantId AS e_tenantId,"
            + " a.id AS a_id, a.positionId AS a_positionId, a.fundId AS a_fundId, a.functionaryId AS a_functionaryId,"
            + " a.functionId AS a_functionId, a.designationId AS a_designationId, a.departmentId AS a_departmentId,"
            + " a.isPrimary AS a_isPrimary, a.fromDate AS a_fromDate, a.toDate AS a_toDate, a.gradeId AS a_gradeId,"
            + " a.govtOrderNumber AS a_govtOrderNumber, a.createdBy AS a_createdBy, a.createdDate AS a_createdDate,"
            + " a.lastModifiedBy AS a_lastModifiedBy, a.lastModifiedDate AS a_lastModifiedDate,"
            + " a.employeeId AS a_employeeId,"
            + " hod.id AS hod_id, hod.departmentId AS hod_departmentId,"
            + " ej.jurisdictionId AS ej_jurisdictionId"
            + " FROM egeis_employee e"
            + " JOIN egeis_assignment a ON e.id = a.employeeId AND a.tenantId = e.tenantId"
            + " LEFT JOIN egeis_hodDepartment hod ON a.id = hod.assignmentId AND hod.tenantId = e.tenantId"
            + " LEFT JOIN egeis_employeeJurisdictions ej ON e.id = ej.employeeId AND ej.tenantId = e.tenantId"
            + " WHERE e.tenantId = :tenantId";

    private static final String REPORT_QUERY = "SELECT e.id AS e_id, e.code AS e_code, e.employeeTypeId AS e_employeeTypeId,"
            + " e.employeeStatus AS e_employeeStatus,e.groupId AS e_groupId,e.maritalStatus AS e_maritalStatus, e.placeOfBirth AS e_placeOfBirth,e.motherTongueId as e_motherTongueId,e.passportNo as e_passportNo, e.gpfNo as e_gpfNo, e.bankId AS e_bankId,"
            + " e.bankBranchId AS e_bankBranchId, e.bankAccount AS e_bankAccount,e.recruitmentModeId as e_recruitmentModeId,e.recruitmentTypeId AS e_recruitmentTypeId,e.recruitmentQuotaId AS e_recruitmentQuotaId," +
            " e.dateOfAppointment as e_dateOfAppointment,e.dateOfJoining AS e_dateOfJoining, e.dateOfRetirement as e_dateOfRetirement,e.retirementAge AS e_retirementAge,"
            + " e.dateOfResignation AS e_dateOfResignation, e.dateOfTermination AS e_dateOfTermination, l.languageId AS l_languageId,  e.tenantId AS e_tenantId"
            + " FROM egeis_employee e LEFT JOIN egeis_employeelanguages l ON e.id = l.employeeId AND l.tenantId = e.tenantId"
            + " WHERE e.tenantId = :tenantId";

    @Autowired
    private ApplicationProperties applicationProperties;

    public String getQueryForListOfEmployeeIds(EmployeeCriteria employeeCriteria, Map<String, Object> namedParameters) {
        StringBuilder selectQuery = new StringBuilder(EMPLOYEE_IDS_QUERY);

        addWhereClause(selectQuery, namedParameters, employeeCriteria, null);
        addOrderByClause(selectQuery, employeeCriteria);
        addPagingClause(selectQuery, namedParameters, employeeCriteria);

        log.debug("Get List Of Employee Ids Query : " + selectQuery);
        return selectQuery.toString();
    }

    public String getQueryForEmployeeReport(BaseRegisterReportRequest baseRegisterReportRequest, Map<String, Object> namedParameters) {
        StringBuilder selectQuery = new StringBuilder(REPORT_QUERY);

        addWhereClauseForReportQuery(selectQuery, namedParameters, baseRegisterReportRequest);
        addPagingClauseForReports(selectQuery, namedParameters, baseRegisterReportRequest.getPageSize(), baseRegisterReportRequest.getPageNumber());


        log.debug("Get Employee Report Query : " + selectQuery);
        return selectQuery.toString();
    }

    private void addWhereClauseForReportQuery(StringBuilder selectQuery, Map<String, Object> namedParameters,
                                              BaseRegisterReportRequest baseRegisterReportRequest) {

        namedParameters.put("tenantId", baseRegisterReportRequest.getTenantId());

        if (!isEmpty(baseRegisterReportRequest.getEmployeeStatus())) {
            selectQuery.append(" AND e.employeeStatus = :employeeStatus");
            namedParameters.put("employeeStatus", baseRegisterReportRequest.getEmployeeStatus());
        }

        if (!isEmpty(baseRegisterReportRequest.getEmployeeType())) {
            selectQuery.append(" AND e.employeeTypeId = :employeeTypeId");
            namedParameters.put("employeeTypeId", baseRegisterReportRequest.getEmployeeType());
        }

        if (!isEmpty(baseRegisterReportRequest.getRecruitmentType())) {
            selectQuery.append(" AND e.recruitmenttypeid = :recruitmentTypeId");
            namedParameters.put("recruitmentTypeId", baseRegisterReportRequest.getRecruitmentType());
        }


    }

    public String getQuery(EmployeeCriteria employeeCriteria, Map<String, Object> namedParameters, List<Long> empIds) {
        StringBuilder selectQuery = new StringBuilder(BASE_QUERY);
        addWhereClause(selectQuery, namedParameters, employeeCriteria, empIds);
        addOrderByClause(selectQuery, employeeCriteria);
        log.debug("Get Employees Query : " + selectQuery);
        return selectQuery.toString();
    }

    private void addWhereClause(StringBuilder selectQuery, Map<String, Object> namedParameters,
                                EmployeeCriteria employeeCriteria, List<Long> empIds) {

        namedParameters.put("tenantId", employeeCriteria.getTenantId());

        if (isEmpty(empIds) && isEmpty(employeeCriteria.getId()) && isEmpty(employeeCriteria.getCode())
                && isEmpty(employeeCriteria.getDepartmentId()) && isEmpty(employeeCriteria.getIsPrimary())
                && isEmpty(employeeCriteria.getDesignationId()) && isEmpty(employeeCriteria.getPositionId())
                && isEmpty(employeeCriteria.getAsOnDate()) && isEmpty(employeeCriteria.getEmployeeStatus())
                && isEmpty(employeeCriteria.getEmployeeType()) && isEmpty(employeeCriteria.getFamilyParticularsPresent())) {
            return;
        }

        if (!isEmpty(empIds)) {
            selectQuery.append(" AND e.id IN (:ids)");
            namedParameters.put("ids", empIds);
        } else if (!isEmpty(employeeCriteria.getId())) {
            selectQuery.append(" AND e.id IN (:ids)");
            namedParameters.put("ids", employeeCriteria.getId());
        }

        if (!isEmpty(employeeCriteria.getCode())) {
            selectQuery.append(" AND e.code = :code");
            namedParameters.put("code", employeeCriteria.getCode());
        }

        if (!isEmpty(employeeCriteria.getDepartmentId())) {
            selectQuery.append(" AND a.departmentId = :departmentId");
            namedParameters.put("departmentId", employeeCriteria.getDepartmentId());
        }

        if (!isEmpty(employeeCriteria.getDesignationId())) {
            selectQuery.append(" AND a.designationId = :designationId");
            namedParameters.put("designationId", employeeCriteria.getDesignationId());
        }

        if (!isEmpty(employeeCriteria.getPositionId())) {
            selectQuery.append(" AND a.positionId = :positionId");
            namedParameters.put("positionId", employeeCriteria.getPositionId());
        }

        if (!isEmpty(employeeCriteria.getAsOnDate())) {
            selectQuery.append(" AND :asOnDate BETWEEN a.fromDate AND a.toDate");
            namedParameters.put("asOnDate", employeeCriteria.getAsOnDate());
        }

        if (!isEmpty(employeeCriteria.getIsPrimary())) {
            selectQuery.append(" AND a.isPrimary = :isPrimary");
            namedParameters.put("isPrimary", employeeCriteria.getIsPrimary());
        }

        if (!isEmpty(employeeCriteria.getEmployeeStatus())) {
            selectQuery.append(" AND e.employeeStatus IN (:employeeStatuses)");
            namedParameters.put("employeeStatuses", employeeCriteria.getEmployeeStatus());
        }

        if (!isEmpty(employeeCriteria.getEmployeeType())) {
            selectQuery.append(" AND e.employeeTypeId IN (:employeeTypes)");
            namedParameters.put("employeeTypes", employeeCriteria.getEmployeeType());
        }

        if (!isEmpty(employeeCriteria.getFamilyParticularsPresent())) {
            if (employeeCriteria.getFamilyParticularsPresent())
                selectQuery.append(" AND e.id IN (SELECT DISTINCT employeeId FROM egeis_nominee)");
            else
                selectQuery.append(" AND e.id NOT IN (SELECT DISTINCT employeeId FROM egeis_nominee)");
        }
    }

    private void addOrderByClause(StringBuilder selectQuery, EmployeeCriteria employeeCriteria) {
        String sortBy = isEmpty(employeeCriteria.getSortBy()) ? "e.id" : employeeCriteria.getSortBy();
        String sortOrder = isEmpty(employeeCriteria.getSortOrder()) ? "ASC" : employeeCriteria.getSortOrder();
        selectQuery.append(" ORDER BY " + sortBy + " " + sortOrder);
    }

    private void addPagingClause(StringBuilder selectQuery, Map<String, Object> preparedStatementValues,
                                 EmployeeCriteria employeeCriteria) {
        // handle limit(also called pageSize) here
        selectQuery.append(" LIMIT :pageSize");
        long pageSize = Integer.parseInt(applicationProperties.empSearchPageSizeDefault());
        if (!isEmpty(employeeCriteria.getPageSize()))
            pageSize = employeeCriteria.getPageSize();
        preparedStatementValues.put("pageSize", pageSize); // Set limit to pageSize

        // handle offset here
        selectQuery.append(" OFFSET :pageNumber");
        int pageNumber = 0; // Default pageNo is zero meaning first page
        if (!isEmpty(employeeCriteria.getPageNumber()))
            pageNumber = employeeCriteria.getPageNumber() < 1 ? 0 : employeeCriteria.getPageNumber() - 1;
        preparedStatementValues.put("pageNumber", pageNumber * pageSize); // Set offset to pageNo * pageSize
    }

    private void addPagingClauseForReports(StringBuilder selectQuery, Map<String, Object> preparedStatementValues,
                                           Integer reportPageSize, Integer reportPageNumber) {
        // handle limit(also called pageSize) here
        selectQuery.append(" LIMIT :pageSize");
        int pageSize = Integer.parseInt(applicationProperties.empSearchPageSizeDefault());
        if (!isEmpty(reportPageSize))
            pageSize = reportPageSize;
        preparedStatementValues.put("pageSize", pageSize); // Set limit to pageSize

        // handle offset here
        selectQuery.append(" OFFSET :pageNumber");
        int pageNumber = 0; // Default pageNo is zero meaning first page
        if (!isEmpty(reportPageNumber))
            pageNumber = reportPageNumber < 1 ? 0 : reportPageNumber - 1;
        preparedStatementValues.put("pageNumber", pageNumber * pageSize); // Set offset to pageNo * pageSize
    }
}