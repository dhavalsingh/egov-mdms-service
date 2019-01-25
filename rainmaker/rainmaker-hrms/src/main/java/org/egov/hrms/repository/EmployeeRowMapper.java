package org.egov.hrms.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.hrms.model.Assignment;
import org.egov.hrms.model.AuditDetails;
import org.egov.hrms.model.DeactivationDetails;
import org.egov.hrms.model.DepartmentalTest;
import org.egov.hrms.model.EducationalQualification;
import org.egov.hrms.model.Employee;
import org.egov.hrms.model.EmployeeDocument;
import org.egov.hrms.model.Jurisdiction;
import org.egov.hrms.model.ServiceHistory;
import org.egov.hrms.model.enums.DeactivationType;
import org.egov.hrms.model.enums.ReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmployeeRowMapper implements ResultSetExtractor<List<Employee>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Employee> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Employee> employeeMap = new HashMap<>();
		while(rs.next()) {
			String currentid = rs.getString("employee_uuid");
			Employee currentEmployee = employeeMap.get(currentid);
			if(null == currentEmployee) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("employee_createdby")).createdDate(rs.getLong("employee_createddate"))
						.lastModifiedBy(rs.getString("employee_lastmodifiedby")).lastModifiedDate(rs.getLong("employee_lastmodifieddate")).build();
				currentEmployee = Employee.builder().id(rs.getLong("employee_id")).uuid(rs.getString("employee_uuid")).tenantId(rs.getString("employee_tenantid"))
						.code(rs.getString("employee_code")).dateOfAppointment(rs.getLong("employee_doa")).active(rs.getBoolean("employee_active"))
						.employeeStatus(rs.getString("employee_status")).employeeType(rs.getString("employee_type")).auditDetails(auditDetails)
						.build();
			}
			addChildrenToEmployee(rs, currentEmployee);
			employeeMap.put(currentid, currentEmployee);
		}
		
		return new ArrayList<>(employeeMap.values());

	}
	
	public void addChildrenToEmployee(ResultSet rs, Employee currentEmployee) {
		setAssignments(rs, currentEmployee);
		setJurisdictions(rs, currentEmployee);
		setEducationDetails(rs, currentEmployee);
		setDeptTests(rs, currentEmployee);
		setServiceHistory(rs, currentEmployee);
		setDocuments(rs, currentEmployee);
		setDeactivationDetails(rs, currentEmployee);
	}
	
	public void setAssignments(ResultSet rs, Employee currentEmployee) {
		try {
			List<Assignment> assignments = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getAssignments()))
				assignments = new ArrayList<Assignment>();
			else
				assignments = currentEmployee.getAssignments();
			
			List<String> ids = assignments.stream().map(Assignment::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString(rs.getString("assignment_uuid")))) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("assignment_createdby")).createdDate(rs.getLong("assignment_createddate"))
						.lastModifiedBy(rs.getString("assignment_lastmodifiedby")).lastModifiedDate(rs.getLong("assignment_lastmodifieddate")).build();
				
				Assignment assignment = Assignment.builder().id(rs.getString("assignment_uuid")).position(rs.getLong("assignment_position")).department(rs.getString("assignment_department"))
				.designation(rs.getString("assignment_designation")).fromDate(rs.getLong("assignment_fromdate")).toDate(rs.getLong("assignment_todate"))
			    .govtOrderNumber(rs.getString("assignment_govtordernumber")).reportingTo(rs.getString("assignment_reportingto")).isHOD(rs.getBoolean("assignment_ishod"))
				.tenantid(rs.getString("assignment_tenantid")).auditDetails(auditDetails).build();
				
				assignments.add(assignment);
				currentEmployee.setAssignments(assignments);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Assignments: ",e);
		}
	}
	
	public void setJurisdictions(ResultSet rs, Employee currentEmployee) {
		try {
			List<Jurisdiction> jurisdictions = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getJurisdictions()))
				jurisdictions = new ArrayList<Jurisdiction>();
			else
				jurisdictions = currentEmployee.getJurisdictions();
			
			List<String> ids = jurisdictions.stream().map(Jurisdiction::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString("jurisdiction_uuid"))) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("jurisdiction_createdby")).createdDate(rs.getLong("jurisdiction_createddate"))
						.lastModifiedBy(rs.getString("jurisdiction_lastmodifiedby")).lastModifiedDate(rs.getLong("jurisdiction_lastmodifieddate")).build();
				
				Jurisdiction jurisdiction = Jurisdiction.builder().id(rs.getString("jurisdiction_uuid")).hierarchy(rs.getString("jurisdiction_hierarchy"))
						.boundary(rs.getString("jurisdiction_boundary")).boundaryType(rs.getString("jurisdiction_boundarytype"))
						.tenantId(rs.getString("jurisdiction_tenantid")).auditDetails(auditDetails).build();
				
				jurisdictions.add(jurisdiction);
				currentEmployee.setJurisdictions(jurisdictions);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Jurisdictions: ",e);
		}
	}
	
	public void setEducationDetails(ResultSet rs, Employee currentEmployee) {
		try {
			List<EducationalQualification> educationDetails = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getEducation()))
				educationDetails = new ArrayList<EducationalQualification>();
			else
				educationDetails = currentEmployee.getEducation();
			List<String> ids = educationDetails.stream().map(EducationalQualification::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString("education_qualification"))) {

				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("education_createdby")).createdDate(rs.getLong("education_createddate"))
						.lastModifiedBy(rs.getString("education_lastmodifiedby")).lastModifiedDate(rs.getLong("education_lastmodifieddate")).build();
				
				EducationalQualification education = EducationalQualification.builder().qualification(rs.getString("education_qualification")).stream(rs.getString("education_stream"))
						.yearOfPassing(rs.getInt("education_yearofpassing")).university(rs.getString("education_university")).remarks(rs.getString("education_remarks"))
						.tenantId(rs.getString("education_tenantid")).auditDetails(auditDetails).build();
				
				educationDetails.add(education);
				currentEmployee.setEducation(educationDetails);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Educational Details: ",e);
		}
	}
	
	public void setDeptTests(ResultSet rs, Employee currentEmployee) {
		try {
			List<DepartmentalTest> tests = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getTests()))
				tests = new ArrayList<DepartmentalTest>();
			else
				tests = currentEmployee.getTests();
			
			List<String> ids = tests.stream().map(DepartmentalTest::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString("depttest_uuid"))) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("depttest_createdby")).createdDate(rs.getLong("depttest_createddate"))
						.lastModifiedBy(rs.getString("depttest_lastmodifiedby")).lastModifiedDate(rs.getLong("depttest_lastmodifieddate")).build();
				
				DepartmentalTest test = DepartmentalTest.builder().id(rs.getString("depttest_uuid")).test(rs.getString("depttest_test")).yearOfPassing(rs.getInt("depttest_yearofpassing"))
						.remarks(rs.getString("depttest_remarks")).tenantId("depttest_tenantid").auditDetails(auditDetails).build();
				
				tests.add(test);
				currentEmployee.setTests(tests);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Departmental Tests: ",e);
		}
	}
	
	public void setServiceHistory(ResultSet rs, Employee currentEmployee) {
		try {
			List<ServiceHistory> history = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getServiceHistory()))
				history = new ArrayList<ServiceHistory>();
			else
				history = currentEmployee.getServiceHistory();
			
			List<String> ids = history.stream().map(ServiceHistory::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString("history_uuid"))) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("history_createdby")).createdDate(rs.getLong("history_createddate"))
						.lastModifiedBy(rs.getString("history_lastmodifiedby")).lastModifiedDate(rs.getLong("history_lastmodifieddate")).build();
				
				ServiceHistory service = ServiceHistory.builder().id(rs.getString("history_uuid")).serviceStatus("history_servicestatus").serviceFrom(rs.getLong("history_servicefrom"))
						.serviceTo(rs.getLong("history_serviceto")).orderNo(rs.getString("history_ordernumber")).isCurrentPosition(rs.getBoolean("history_iscurrentposition"))
						.location(rs.getString("history_location")).tenantId(rs.getString("history_tenantid")).auditDetails(auditDetails).build();
				
				history.add(service);
				currentEmployee.setServiceHistory(history);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Service History: ",e);
		}
	
	}
	
	public void setDocuments(ResultSet rs, Employee currentEmployee) {
		try {
			List<EmployeeDocument> documents = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getDocuments()))
				documents = new ArrayList<EmployeeDocument>();
			else
				documents = currentEmployee.getDocuments();
			
			List<String> ids = documents.stream().map(EmployeeDocument::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString("docs_uuid"))) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("docs_createdby")).createdDate(rs.getLong("docs_createddate"))
						.lastModifiedBy(rs.getString("docs_lastmodifiedby")).lastModifiedDate(rs.getLong("docs_lastmodifieddate")).build();
				
				EmployeeDocument document = EmployeeDocument.builder().id(rs.getString("docs_uuid")).documentId(rs.getString("docs_documentid"))
						.documentName(rs.getString("docs_documentname")).referenceType(ReferenceType.valueOf(rs.getString("docs_referencetype")))
						.referenceId(rs.getString("docs_referenceid")).tenantId(rs.getString("docs_tenantid")).auditDetails(auditDetails).build();
				
				documents.add(document);
				currentEmployee.setDocuments(documents);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Service History: ",e);
		}
	}
	
	public void setDeactivationDetails(ResultSet rs, Employee currentEmployee) {
		try {
			List<DeactivationDetails> deactDetails = new ArrayList<>();
			if(CollectionUtils.isEmpty(currentEmployee.getDeactivationDetails()))
				deactDetails = new ArrayList<DeactivationDetails>();
			else
				deactDetails = currentEmployee.getDeactivationDetails();
			
			List<String> ids = deactDetails.stream().map(DeactivationDetails::getId).collect(Collectors.toList());
			if(!ids.contains(rs.getString("deact_uuid"))) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("deact_createdby")).createdDate(rs.getLong("deact_createddate"))
						.lastModifiedBy(rs.getString("deact_lastmodifiedby")).lastModifiedDate(rs.getLong("deact_lastmodifieddate")).build();
				
				DeactivationDetails deactDetail = DeactivationDetails.builder().id(rs.getString("deact_uuid")).reasonForDeactivation(rs.getString("deact_reasonfordeactivation"))
						.effectiveFrom(rs.getLong("deact_effectivefrom")).orderNo(rs.getString("deact_ordernumber")).typeOfDeactivation(DeactivationType.valueOf(rs.getString("deact_typeofdeactivation")))
						.tenantId(rs.getString("deact_tenantid")).auditDetails(auditDetails).build();
				
				deactDetails.add(deactDetail);
				currentEmployee.setDeactivationDetails(deactDetails);
			}
		}catch(Exception e) {
			log.error("Error in row mapper while mapping Service History: ",e);
		}
	}

}