/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any Long of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.collection.consumer.service;

import org.egov.collection.consumer.config.PropertiesManager;
import org.egov.collection.consumer.model.Receipt;
import org.egov.collection.consumer.model.ReceiptRequest;
import org.egov.collection.consumer.model.ReceiptResponse;
import org.egov.collection.consumer.model.VoucherResponse;
import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReceiptService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PropertiesManager propertiesManager;

    public ReceiptResponse updateReceipt(ReceiptRequest receiptRequest, VoucherResponse voucherResponse) {

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setAuthToken(tokenService.generateAdminToken(receiptRequest.getTenantId()));

        Receipt receipt = receiptRequest.getReceipt().get(0);
        receipt.getBill().get(0).getBillDetails().get(0)
                .setVoucherHeader(voucherResponse.getVouchers().get(0).getVoucherNumber());

        receiptRequest.setRequestInfo(requestInfo);
        LOGGER.info("call:" + propertiesManager.getHostUrl() + propertiesManager.getReceiptsUpdate());
        return restTemplate.postForObject(propertiesManager.getHostUrl() + propertiesManager.getReceiptsUpdate(), receiptRequest,
                ReceiptResponse.class);
    }

}
