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
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.demand.web.controller;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.demand.model.DemandCriteria;
import org.egov.demand.model.DemandDetailCriteria;
import org.egov.demand.service.DemandService;
import org.egov.demand.web.contract.DemandRequest;
import org.egov.demand.web.contract.DemandResponse;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.egov.demand.web.contract.factory.ResponseFactory;
import org.egov.demand.web.validator.DemandValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/demand")
@Slf4j
public class DemandController {

	private static final Logger logger = LoggerFactory.getLogger(DemandController.class);

	@Autowired
	private DemandService demandService;

	@Autowired
	private ResponseFactory responseFactory;

	@Autowired
	private DemandValidator demandValidator;

	/**
	 * API to create demands
	 *
	 * @param demandRequest
	 * @param bindingResult
	 * @return ResponseEntity<?>
	 */

	@PostMapping("_create")
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody @Valid DemandRequest demandRequest, BindingResult bindingResult) {
		logger.info("the demand request object : " + demandRequest);
		RequestInfo requestInfo = demandRequest.getRequestInfo();
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo), HttpStatus.BAD_REQUEST);
		}
		demandValidator.validate(demandRequest, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo), HttpStatus.BAD_REQUEST);
		}
		DemandResponse demandResponse =  demandService.create(demandRequest);
		logger.info("the Response Object from service : "+demandResponse);
		return new ResponseEntity<>(demandResponse, HttpStatus.CREATED);
	}

	@PostMapping("_update")
	public ResponseEntity<?> update(@RequestBody @Valid DemandRequest demandRequest, BindingResult bindingResult) {

		RequestInfo requestInfo = demandRequest.getRequestInfo();
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo),
					HttpStatus.BAD_REQUEST);
		}

		demandValidator.validate(demandRequest, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(demandService.updateAsync(demandRequest), HttpStatus.CREATED);
	}

	@PostMapping("collection/_update")
	public ResponseEntity<?> updateCollection(@RequestBody @Valid DemandRequest demandRequest, BindingResult bindingResult) {

		RequestInfo requestInfo = demandRequest.getRequestInfo();
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo), HttpStatus.BAD_REQUEST);
		}

		demandValidator.validate(demandRequest, bindingResult);
		return new ResponseEntity<>(demandService.updateCollection(demandRequest), HttpStatus.CREATED);
	}

	@PostMapping("_search")
	public ResponseEntity<?> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
									@ModelAttribute @Valid DemandCriteria demandCriteria, BindingResult bindingResult) {

		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(demandService.getDemands(demandCriteria, requestInfo), HttpStatus.OK);
	}

	@PostMapping("/demanddetail/_search")
	public ResponseEntity<?> demandDetailSearch(@RequestBody RequestInfoWrapper requestInfoWrapper,
												@ModelAttribute @Valid DemandDetailCriteria demandDetailCriteria, BindingResult bindingResult) {

		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(responseFactory.getErrorResponse(bindingResult, requestInfo), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(demandService.getDemandDetails(demandDetailCriteria, requestInfo), HttpStatus.OK);
	}
}
