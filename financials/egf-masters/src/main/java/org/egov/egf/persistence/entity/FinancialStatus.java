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

package org.egov.egf.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "egeis_egfStatus")
@SequenceGenerator(name = FinancialStatus.SEQ_EGEIS_EGFSTATUS, sequenceName = FinancialStatus.SEQ_EGEIS_EGFSTATUS, allocationSize = 1)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FinancialStatus extends AbstractAuditable {

	private static final long serialVersionUID = -8641767461447916679L;

	public static final String SEQ_EGEIS_EGFSTATUS = "SEQ_EGEIS_EGFSTATUS";
	
	@Id
	@GeneratedValue(generator = FinancialStatus.SEQ_EGEIS_EGFSTATUS, strategy = GenerationType.SEQUENCE)
	@NotNull
	private Long id;

	@NotNull
	@Size(min = 3, max = 50)
	private String objectName;

	@NotNull
	@Size(min = 3, max = 20)
	private String code;

	@NotNull
	@Size(min = 3, max = 250)
	private String description;

}