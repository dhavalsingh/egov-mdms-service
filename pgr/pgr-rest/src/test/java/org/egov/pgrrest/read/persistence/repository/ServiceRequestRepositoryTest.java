package org.egov.pgrrest.read.persistence.repository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pgrrest.common.contract.ServiceRequest;
import org.egov.pgrrest.common.contract.SevaRequest;
import org.egov.pgrrest.common.entity.Complaint;
import org.egov.pgrrest.common.repository.ComplaintJpaRepository;
import org.egov.pgrrest.read.domain.model.ServiceRequestSearchCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceRequestRepositoryTest {

    @Mock
    private ComplaintJpaRepository complaintJpaRepository;

    @Mock
    private ServiceRequestMessageQueueRepository serviceRequestMessageQueueRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Captor
    private ArgumentCaptor<SevaRequest> sevaRequestArgumentCaptor;

    private ServiceRequestRepository complaintRepository;

    @Before
    public void setUp() throws Exception {
        complaintRepository =
            new ServiceRequestRepository(complaintJpaRepository, serviceRequestMessageQueueRepository, submissionRepository);
    }

    @Test
    public void test_complaint_repository_should_call_jpa_repository_to_save_the_complaint() {
        RequestInfo requestInfo = new RequestInfo();
        ServiceRequest serviceRequest = new ServiceRequest();
        SevaRequest sevaRequest = new SevaRequest(requestInfo, serviceRequest);

       complaintRepository.save(sevaRequest);

        verify(serviceRequestMessageQueueRepository).save(sevaRequestArgumentCaptor.capture());

        SevaRequest actual = sevaRequestArgumentCaptor.getValue();
        assertThat(actual.getRequestInfo().getAction()).isEqualTo("POST");
        assertThat(actual.getServiceRequest().getCreatedDate()).isNotNull();
    }

    @Test
    public void test_complaint_repository_should_call_jpa_repository_to_update_the_complaint() {
        RequestInfo requestInfo = new RequestInfo();
        ServiceRequest serviceRequest = new ServiceRequest();
        SevaRequest sevaRequest = new SevaRequest(requestInfo, serviceRequest);

        complaintRepository.update(sevaRequest);

        verify(serviceRequestMessageQueueRepository).save(sevaRequestArgumentCaptor.capture());
        SevaRequest actual = sevaRequestArgumentCaptor.getValue();
        assertThat(actual.getRequestInfo().getAction()).isEqualTo("PUT");
        assertThat(actual.getServiceRequest().getLastModifiedDate()).isNotNull();
    }

    @Test
    public void test_find_all_complaints_using_specification() {
        final Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
        ServiceRequestSearchCriteria serviceRequestSearchCriteria = mock(ServiceRequestSearchCriteria.class);
        Complaint complaintEntityMock1 = mock(Complaint.class);
        Complaint complaintEntityMock2 = mock(Complaint.class);
        org.egov.pgrrest.read.domain.model.ServiceRequest complaintModelMock1 = mock(org.egov.pgrrest.read.domain.model.ServiceRequest.class);
       org.egov.pgrrest.read.domain.model.ServiceRequest complaintModelMock2 = mock(org.egov.pgrrest.read.domain.model.ServiceRequest.class);

        when(complaintEntityMock1.toDomain()).thenReturn(complaintModelMock1);
        when(complaintEntityMock2.toDomain()).thenReturn(complaintModelMock2);

        when(complaintJpaRepository.findAll(Matchers.<Specification<Complaint>> any(), eq(sort)))
                .thenReturn(Arrays.asList(complaintEntityMock1, complaintEntityMock2));

        List<org.egov.pgrrest.read.domain.model.ServiceRequest> actual = complaintRepository.findAll(serviceRequestSearchCriteria);

        assertThat(Arrays.asList(complaintModelMock1, complaintModelMock2)).isEqualTo(actual);
    }

    @Test
    public void test_complaint_repository_should_get_complaints_modified_for_a_user_from_jpa_repository() {
        Date lastAccessedTime = Calendar.getInstance().getTime();
        Complaint complaintEntityMock1 = mock(Complaint.class);
        Complaint complaintEntityMock2 = mock(Complaint.class);
        org.egov.pgrrest.read.domain.model.ServiceRequest complaintModelMock1 = mock(
                org.egov.pgrrest.read.domain.model.ServiceRequest.class);
        org.egov.pgrrest.read.domain.model.ServiceRequest complaintModelMock2 = mock(
                org.egov.pgrrest.read.domain.model.ServiceRequest.class);

        when(complaintEntityMock1.toDomain()).thenReturn(complaintModelMock1);
        when(complaintEntityMock2.toDomain()).thenReturn(complaintModelMock2);

        when(complaintJpaRepository.getAllModifiedComplaintsForCitizen(1L, "tenantId"))
                .thenReturn(Arrays.asList(complaintEntityMock1, complaintEntityMock2));

        List<org.egov.pgrrest.read.domain.model.ServiceRequest> actual = complaintRepository
                .getAllModifiedServiceRequestsForCitizen(1L, "tenantId");

        assertThat(Arrays.asList(complaintModelMock1, complaintModelMock2)).isEqualTo(actual);
    }
} 
