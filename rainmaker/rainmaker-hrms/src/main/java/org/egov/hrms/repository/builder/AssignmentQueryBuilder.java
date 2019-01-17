/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
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
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.hrms.repository.builder;

import lombok.extern.slf4j.Slf4j;
import org.egov.hrms.config.ApplicationProperties;
import org.egov.hrms.web.contract.AssignmentGetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
public class AssignmentQueryBuilder {

	@Autowired
	private ApplicationProperties applicationProperties;

	/**
	 * FIXME : Had to select fromDate along with id as the default selection order is fromDate DESC by default.
	 * The sorting logic will not work apart from order by id or fromDate because we can't use orderBy
	 * on the non-selected columns with DISTINCT, which will be problematic in any sort conditions.
	 * Also, we should not use the sorting logic in 2nd query, because then we may not get the desired result.
	 * As it'll then sort the limited result having ids generated by this query. Rather than sorting the actual
	 * data & limiting the records.
	 * So, find a better solution for this.
	 */
	private static final String ASSIGNMENT_IDS_QUERY = "SELECT DISTINCT a.id as id, a.fromDate as fromDate"
			+ " FROM egeis_assignment a"
			+ " JOIN egeis_employee e ON e.id = a.employeeId AND e.tenantId = a.tenantId"
			+ " LEFT JOIN egeis_hodDepartment hod ON a.id = hod.assignmentId AND hod.tenantId = a.tenantId"
			+ " WHERE e.id = :employeeId AND a.tenantId = :tenantId";

	private static final String BASE_QUERY = "SELECT a.id as a_id, a.positionId as a_positionId, a.fundId as a_fundId,"
			+ " a.functionaryId as a_functionaryId, a.functionId as a_functionId, a.designationId as a_designationId,"
			+ " a.departmentId as a_departmentId, a.isPrimary as a_isPrimary, a.fromDate as a_fromDate,"
			+ " a.toDate as a_toDate, a.gradeId as a_gradeId, a.govtOrderNumber as a_govtOrderNumber,"
			+ " a.createdBy as a_createdBy, a.createdDate as a_createdDate, a.lastModifiedBy as a_lastModifiedBy,"
			+ " a.lastModifiedDate as a_lastModifiedDate, a.tenantId as a_tenantId,"
			+ " hod.id as hod_id, hod.departmentId as hod_departmentId"
			+ " FROM egeis_assignment a"
			+ " JOIN egeis_employee e ON e.id = a.employeeId AND e.tenantId = a.tenantId"
			+ " LEFT JOIN egeis_hodDepartment hod ON a.id = hod.assignmentId AND hod.tenantId = a.tenantId"
			+ " WHERE e.id = :employeeId AND a.tenantId = :tenantId";

	public String getQueryForListOfAssignmentIds(Long employeeId, AssignmentGetRequest assignmentGetRequest,
												 Map<String, Object> namedParameters) {
		StringBuilder selectQuery = new StringBuilder(ASSIGNMENT_IDS_QUERY);

		addWhereClause(employeeId, selectQuery, namedParameters, assignmentGetRequest, null);
		addOrderByClause(selectQuery, assignmentGetRequest);
		addPagingClause(selectQuery, namedParameters, assignmentGetRequest);

		log.debug("Query For List Of Assignment Ids : " + selectQuery);
		return selectQuery.toString();
	}

	public String getQuery(Long employeeId, AssignmentGetRequest assignmentGetRequest,
						   Map<String, Object> namedParameters, List<Long> assignmentIds) {
		StringBuilder selectQuery = new StringBuilder(BASE_QUERY);
		addWhereClause(employeeId, selectQuery, namedParameters, assignmentGetRequest, assignmentIds);
		addOrderByClause(selectQuery, assignmentGetRequest);
		log.debug("Query For Assignments : " + selectQuery);
		return selectQuery.toString();
	}

	private void addWhereClause(Long employeeId, StringBuilder selectQuery, Map<String, Object> namedParameters,
								AssignmentGetRequest assignmentGetRequest, List<Long> assignmentIds) {

		namedParameters.put("employeeId", employeeId);
		namedParameters.put("tenantId", assignmentGetRequest.getTenantId());

		if (isEmpty(assignmentIds) && isEmpty(assignmentGetRequest.getAssignmentId())
				&& isEmpty(assignmentGetRequest.getIsPrimary()) && isEmpty(assignmentGetRequest.getAsOnDate()))
			return;

		if (!isEmpty(assignmentIds)) {
			selectQuery.append(" AND a.id IN (:ids)");
			namedParameters.put("ids", assignmentIds);
		} else if (!isEmpty(assignmentGetRequest.getAssignmentId())) {
			selectQuery.append(" AND a.id IN (:ids)");
			namedParameters.put("ids", assignmentGetRequest.getAssignmentId());
		}

		if (!isEmpty(assignmentGetRequest.getIsPrimary())) {
			selectQuery.append(" AND a.isPrimary = :isPrimary");
			namedParameters.put("isPrimary", assignmentGetRequest.getIsPrimary());
		}

		if (!isEmpty(assignmentGetRequest.getAsOnDate())) {
			selectQuery.append(" AND :asOnDate BETWEEN a.fromDate AND a.toDate");
			namedParameters.put("asOnDate", assignmentGetRequest.getAsOnDate());
		}
	}

	private void addOrderByClause(StringBuilder selectQuery, AssignmentGetRequest assignmentGetRequest) {
		String sortBy = (isEmpty(assignmentGetRequest.getSortBy()) ? "a.fromDate" : assignmentGetRequest.getSortBy());
		String sortOrder = (isEmpty(assignmentGetRequest.getSortOrder()) ? "DESC" : assignmentGetRequest.getSortOrder());
		selectQuery.append(" ORDER BY " + sortBy + " " + sortOrder);
	}

	private void addPagingClause(StringBuilder selectQuery, Map<String, Object> preparedStatementValues,
								 AssignmentGetRequest assignmentGetRequest) {
		// handle limit(also called pageSize) here
		selectQuery.append(" LIMIT :pageSize");
		long pageSize = Integer.parseInt(applicationProperties.empSearchPageSizeDefault());
		if (!isEmpty(assignmentGetRequest.getPageSize()))
			pageSize = assignmentGetRequest.getPageSize();
		preparedStatementValues.put("pageSize", pageSize); // Set limit to pageSize

		// handle offset here
		selectQuery.append(" OFFSET :pageNumber");
		int pageNumber = 0; // Default pageNo is zero meaning first page
		if (!isEmpty(assignmentGetRequest.getPageNumber()))
			pageNumber = assignmentGetRequest.getPageNumber() - 1;
		preparedStatementValues.put("pageNumber", pageNumber * pageSize); // Set offset to pageNo * pageSize
	}
}
