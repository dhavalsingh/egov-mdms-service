package org.egov.pt.calculator.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.egov.pt.calculator.util.CalculatorConstants;
import org.egov.pt.calculator.util.CalculatorUtils;
import org.egov.pt.calculator.web.models.TaxHeadEstimate;
import org.egov.pt.calculator.web.models.collections.Receipt;
import org.egov.pt.calculator.web.models.demand.Bill;
import org.egov.pt.calculator.web.models.demand.BillAccountDetail;
import org.egov.pt.calculator.web.models.demand.BillDetail;
import org.egov.pt.calculator.web.models.demand.BillRequest;
import org.egov.pt.calculator.web.models.demand.BillResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.minidev.json.JSONArray;

/**
 * Deals with the functionality that are performed 
 * 
 * before the time of bill generation(before payment)  
 * 
 * or at the time of bill apportioning(after payment)
 * 
 * @author kavi elrey
 *
 */
@Service
public class PayService {

	@Autowired
	private CalculatorUtils utils;

	@Autowired
	private MasterDataService mDService;
	
	/**
	 * Updates the incoming demand with latest rebate, penalty and interest values if applicable
	 * 
	 * If the demand details are not already present then new demand details will be added
	 * 
	 * @param assessmentYear
	 * @return
	 */
	public Map<String, BigDecimal> applyPenaltyRebateAndInterest(BigDecimal taxAmt, BigDecimal collectedPtTax,
			 String assessmentYear, Map<String, JSONArray> timeBasedExmeptionMasterMap,List<Receipt> receipts) {

		if (BigDecimal.ZERO.compareTo(taxAmt) >= 0)
			return null;

		Map<String, BigDecimal> estimates = new HashMap<>();

		BigDecimal rebate = BigDecimal.ZERO.compareTo(collectedPtTax) == 0 ? getRebate(taxAmt, assessmentYear,
				timeBasedExmeptionMasterMap.get(CalculatorConstants.REBATE_MASTER)) : BigDecimal.ZERO;

		BigDecimal penalty = BigDecimal.ZERO;
		BigDecimal interest = BigDecimal.ZERO;

		if (rebate.equals(BigDecimal.ZERO)) {
			penalty = getPenalty(taxAmt, assessmentYear, timeBasedExmeptionMasterMap.get(CalculatorConstants.PENANLTY_MASTER));
			interest = getInterest(taxAmt.subtract(collectedPtTax), assessmentYear,
					timeBasedExmeptionMasterMap.get(CalculatorConstants.INTEREST_MASTER),receipts);
		}

		estimates.put(CalculatorConstants.PT_TIME_REBATE, rebate.setScale(2, 2).negate());
		estimates.put(CalculatorConstants.PT_TIME_PENALTY, penalty.setScale(2, 2));
		estimates.put(CalculatorConstants.PT_TIME_INTEREST, interest.setScale(2, 2));
		return estimates;
	}

	/**
	 * Returns the Amount of Rebate that can be applied on the given tax amount for
	 * the given period
	 * 
	 * @param taxAmt
	 * @param assessmentYear
	 * @return
	 */
	public BigDecimal getRebate(BigDecimal taxAmt, String assessmentYear, JSONArray rebateMasterList) {

		BigDecimal rebateAmt = BigDecimal.ZERO;
		Map<String, Object> rebate = mDService.getApplicableMaster(assessmentYear, rebateMasterList);

		if (null == rebate) return rebateAmt;

		String[] time = ((String) rebate.get(CalculatorConstants.ENDING_DATE_APPLICABLES)).split("/");
		Calendar cal = Calendar.getInstance();
		setDateToCalendar(assessmentYear, time, cal);

		if (cal.getTimeInMillis() > System.currentTimeMillis())
			rebateAmt = mDService.calculateApplicables(taxAmt, rebate);
		
		return rebateAmt;
	}

	/**
	 * Returns the Amount of penalty that has to be applied on the given tax amount for the given period
	 * 
	 * @param taxAmt
	 * @param assessmentYear
	 * @return
	 */
	public BigDecimal getPenalty(BigDecimal taxAmt, String assessmentYear, JSONArray penaltyMasterList) {

		BigDecimal penaltyAmt = BigDecimal.ZERO;
		Map<String, Object> penalty = mDService.getApplicableMaster(assessmentYear, penaltyMasterList);
		if (null == penalty) return penaltyAmt;

		String[] time = getStartTime(assessmentYear,penalty);
		Calendar cal = Calendar.getInstance();
		setDateToCalendar(time, cal);

		if (cal.getTimeInMillis() < System.currentTimeMillis()) 
			penaltyAmt = mDService.calculateApplicables(taxAmt, penalty);

		return penaltyAmt;
	}

	/**
	 * Returns the Amount of penalty that has to be applied on the given tax amount for the given period
	 * 
	 * @param taxAmt
	 * @param assessmentYear
	 * @return
	 */
	public BigDecimal getInterest(BigDecimal taxAmt, String assessmentYear,
								  JSONArray interestMasterList, List<Receipt> receipts) {

		BigDecimal interestAmt = BigDecimal.ZERO;
		Map<String, Object> interestMap = mDService.getApplicableMaster(assessmentYear, interestMasterList);
		if (null == interestMap)
			return interestAmt;

		String[] time = getStartTime(assessmentYear,interestMap);

		Calendar cal = Calendar.getInstance();
		setDateToCalendar(time, cal);
		long current = System.currentTimeMillis();
		long interestStart = cal.getTimeInMillis();

		if(interestStart < current){

			if(CollectionUtils.isEmpty(receipts)){
				long numberOfDaysInMillies = current - interestStart;
				BigDecimal interestCalculated = calculateInterest(numberOfDaysInMillies,taxAmt,interestMap);
				return interestCalculated;
			}
			else{
				BigDecimal applicableAmount;
				BigDecimal interestCalculated;
				int numberOfPeriods = receipts.size()+1;
				long numberOfDaysInMillies;
				Receipt receipt;

				for(int i = 0 ; i<numberOfPeriods; i++){

					if(i!=numberOfPeriods-1)
					 receipt = receipts.get(i);
					else receipt = receipts.get(i-1);
					Bill bill = receipt.getBill().get(0);
					BillDetail detail = receipt.getBill().get(0).getBillDetails().get(0);

					if(i==0){
						applicableAmount = taxAmt;
						numberOfDaysInMillies = detail.getReceiptDate() - interestStart;
						interestCalculated = calculateInterest(numberOfDaysInMillies,applicableAmount,interestMap);
					}
					else if(i==numberOfPeriods-1){
						applicableAmount = getRemainingAmount(receipt);;
						numberOfDaysInMillies = detail.getReceiptDate() - current;
						interestCalculated = calculateInterest(numberOfDaysInMillies,applicableAmount,interestMap);
					}
					else {
						Receipt receiptPrev = receipts.get(i-1);
						Bill billPrev = receiptPrev.getBill().get(0);
						BillDetail detailPrev = billPrev.getBillDetails().get(0);
						applicableAmount = getRemainingAmount(receiptPrev);
						numberOfDaysInMillies = detail.getReceiptDate() - detailPrev.getReceiptDate();
						interestCalculated = calculateInterest(numberOfDaysInMillies,applicableAmount,interestMap);
					}
					interestAmt = interestAmt.add(interestCalculated);
				}
			}
		}
		return interestAmt;
	}
	
	/**
	 * Return the BillResponse which contains apportioned bills
	 * 
	 * @param billRequest
	 * @return
	 */
	public BillResponse apportionBills(BillRequest billRequest) {

		billRequest.getBills().forEach(bill -> bill.getBillDetails().forEach(detail -> {
			BigDecimal amtPaid = detail.getCollectedAmount();
			apportionBillAccountDetails(detail.getBillAccountDetails(), amtPaid);
		}));
		return BillResponse.builder().bill(billRequest.getBills()).build();
	}

	/**
	 * Apportions the amount paid to the bill account details based on the tax head codes priority
	 * 
	 * The Anonymous comparator uses the priority map to decide the precedence
	 * 
	 * Once the list is sorted based on precedence the amount will apportioned appropriately
	 * 
	 * @param billAccountDetails
	 * @param amtPaid
	 */
	private void apportionBillAccountDetails(List<BillAccountDetail> billAccountDetails, BigDecimal amtPaid) {

		Collections.sort(billAccountDetails, new Comparator<BillAccountDetail>() {
			final Map<String, Integer> taxHeadpriorityMap = utils.getTaxHeadApportionPriorityMap();

			@Override
			public int compare(BillAccountDetail arg0, BillAccountDetail arg1) {
				String taxHead0 = arg0.getTaxHeadCode();
				String taxHead1 = arg1.getTaxHeadCode();

				Integer value0 = taxHeadpriorityMap.get(CalculatorConstants.MAX_PRIORITY_VALUE);
				Integer value1 = taxHeadpriorityMap.get(CalculatorConstants.MAX_PRIORITY_VALUE);

				if (null != taxHeadpriorityMap.get(taxHead0))
					value0 = taxHeadpriorityMap.get(taxHead0);

				if (null != taxHeadpriorityMap.get(taxHead1))
					value1 = taxHeadpriorityMap.get(taxHead1);

				return value0 - value1;
			}
		});

		/*
		 * amtRemaining is the total amount left to apportioned if amtRemaining is zero
		 * then break the for loop
		 * 
		 * if the amountToBePaid is greater then amtRemaining then set amtRemaining to
		 * collectedAmount(creditAmount)
		 * 
		 * if the amtRemaining is Greater than amountToBeCollected then subtract
		 * amtToBecollected from amtRemaining and set the same to
		 * collectedAmount(creditAmount)
		 */
		BigDecimal amtRemaining = amtPaid;
		for (BillAccountDetail billAccountDetail : billAccountDetails) {
			if (BigDecimal.ZERO.compareTo(amtRemaining) < 0) {
				BigDecimal amtToBePaid = billAccountDetail.getAmount();
				if (amtToBePaid.compareTo(amtRemaining) >= 0) {
					billAccountDetail.setAdjustedAmount(amtRemaining);
					amtRemaining = BigDecimal.ZERO;
				} else if (amtToBePaid.compareTo(amtRemaining) < 0) {
					billAccountDetail.setAdjustedAmount(amtToBePaid);
					amtRemaining = amtRemaining.subtract(amtToBePaid);
				}
			} else break;
		}
	}

	/**
	 * Sets the date in to calendar based on the month and date value present in the time array
	 * 
	 * @param assessmentYear
	 * @param time
	 * @param cal
	 */
	private void setDateToCalendar(String assessmentYear, String[] time, Calendar cal) {
		
		cal.clear();
		Integer day = Integer.valueOf(time[0]);
		Integer month = Integer.valueOf(time[1])-1;
		// One is subtracted because calender reads january as 0
		Integer year = Integer.valueOf(assessmentYear.split("-")[0]);
		if (month < 3) year += 1;
		cal.set(year, month, day);
	}


	/**
	 * Overloaded method
	 * Sets the date in to calendar based on the month and date value present in the time array*
	 * @param time
	 * @param cal
	 */
	private void setDateToCalendar(String[] time, Calendar cal) {

		cal.clear();
		Integer day = Integer.valueOf(time[0]);
		Integer month = Integer.valueOf(time[1])-1;
		// One is subtracted because calender reads january as 0
		Integer year = Integer.valueOf(time[2]);
		cal.set(year, month, day);
	}

	/**
	 * Decimal is ceiled for all the tax heads
	 * 
	 * if the decimal is greater than 0.5 upper bound will be applied
	 * 
	 * else if decimal is lesser than 0.5 lower bound is applied
	 * 
	 */
	public TaxHeadEstimate roundOfDecimals(BigDecimal creditAmount, BigDecimal debitAmount) {

		BigDecimal roundOffPos = BigDecimal.ZERO;
		BigDecimal roundOffNeg = BigDecimal.ZERO;

		BigDecimal result = creditAmount.add(debitAmount);
		BigDecimal roundOffAmount = result.setScale(2, 2);
		BigDecimal reminder = roundOffAmount.remainder(BigDecimal.ONE);

		if (reminder.doubleValue() >= 0.5)
			roundOffPos = roundOffPos.add(BigDecimal.ONE.subtract(reminder));
		else if (reminder.doubleValue() < 0.5)
			roundOffNeg = roundOffNeg.add(reminder).negate();

		if (roundOffPos.doubleValue() > 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffPos)
					.taxHeadCode(CalculatorConstants.PT_ROUNDOFF).build();
		else if (roundOffNeg.doubleValue() < 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffNeg)
					.taxHeadCode(CalculatorConstants.PT_ROUNDOFF).build();
		else
			return null;
	}

	public TaxHeadEstimate roundOfDecimals(BigDecimal amount) {

		BigDecimal roundOffPos = BigDecimal.ZERO;
		BigDecimal roundOffNeg = BigDecimal.ZERO;

		BigDecimal roundOffAmount = amount.setScale(2, 2);
		BigDecimal reminder = roundOffAmount.remainder(BigDecimal.ONE);

		if (reminder.doubleValue() >= 0.5)
			roundOffPos = roundOffPos.add(BigDecimal.ONE.subtract(reminder));
		else if (reminder.doubleValue() < 0.5)
			roundOffNeg = roundOffNeg.add(reminder).negate();

		if (roundOffPos.doubleValue() > 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffPos)
					.taxHeadCode(CalculatorConstants.PT_ROUNDOFF).build();
		else if (roundOffNeg.doubleValue() < 0)
			return TaxHeadEstimate.builder().estimateAmount(roundOffNeg)
					.taxHeadCode(CalculatorConstants.PT_ROUNDOFF).build();
		else
			return null;
	}


	/**
	 * Fetch the fromFY and take the starting year of financialYear
	 * calculate the difference between the start of assessment financial year and fromFY
	 * Add the difference in year to the year in the starting day
	 * eg: Assessment year = 2017-18 and interestMap fetched from master due to fallback have fromFY = 2015-16
	 * and startingDay = 01/04/2016. Then diff = 2017-2015 = 2
	 * Therefore the starting day will be modified from 01/04/2016 to 01/04/2018
	 * @param assessmentYear Year of the assessment
	 * @param interestMap The applicable master data
	 * @return list of string with 0'th element as day, 1'st as month and 2'nd as year
	 */
	private String[] getStartTime(String assessmentYear,Map<String, Object> interestMap){
		String financialYearOfApplicableEntry = ((String) interestMap.get(CalculatorConstants.FROMFY_FIELD_NAME)).split("-")[0];
		Integer diffInYear = Integer.valueOf(assessmentYear.split("-")[0]) - Integer.valueOf(financialYearOfApplicableEntry);
		String startDay = ((String) interestMap.get(CalculatorConstants.STARTING_DATE_APPLICABLES));
		Integer yearOfStartDayInApplicableEntry = Integer.valueOf((startDay.split("/")[2]));
		startDay = startDay.replace(String.valueOf(yearOfStartDayInApplicableEntry),String.valueOf(yearOfStartDayInApplicableEntry+diffInYear));
		String[] time = startDay.split("/");
		return time;
	}



	/**
	 * Calculates the interest based on the given parameters
	 * @param numberOfDaysInMillies Time for which interest has to be calculated
	 * @param applicableAmount The amount on which interest is applicable
	 * @param interestMap The interest master data
	 * @return interest calculated
	 */
	private BigDecimal calculateInterest(long numberOfDaysInMillies,BigDecimal applicableAmount,Map<String, Object> interestMap){
		BigDecimal interestAmt;
		BigDecimal noOfDays = BigDecimal.valueOf((TimeUnit.MILLISECONDS.toDays(Math.abs(numberOfDaysInMillies))));
		if(BigDecimal.ONE.compareTo(noOfDays) <= 0) noOfDays = noOfDays.add(BigDecimal.ONE);
		interestAmt = mDService.calculateApplicables(applicableAmount, interestMap);
		return interestAmt.multiply(noOfDays.divide(BigDecimal.valueOf(365), 6, 5));
	}


	/**
	 * Returns the amount remaining for the given receipt
	 * @param receipt
	 * @return
	 */
	private BigDecimal getRemainingAmount(Receipt receipt){
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal amountPaid = BigDecimal.ZERO;
		List<BillDetail> billDetails = new LinkedList<>();

		receipt.getBill().forEach(bill -> {
			billDetails.addAll(bill.getBillDetails());
		});

		for(BillDetail billDetail : billDetails){
			totalAmount.add(billDetail.getTotalAmount());
			amountPaid.add(billDetail.getAmountPaid());
		}
		return totalAmount.subtract(amountPaid);
	}

}
