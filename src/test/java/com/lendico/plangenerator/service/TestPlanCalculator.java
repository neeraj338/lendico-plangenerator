package com.lendico.plangenerator.service;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.lendico.plangenerator.model.Loan;
import com.lendico.plangenerator.model.PaymentInfo;

public class TestPlanCalculator {
	
	private PaymentCalculator planCalculator = new PaymentCalculator();
	
	@Test
	public void testCreateInstallmentPlan() {
		PaymentInfo installmentPayPlan = planCalculator.createInstallmentPayInfo(LocalDateTime.now(), 5.0d, 5000, 1);
		
		Assert.assertThat(installmentPayPlan.getBorrowerPaymentAmount(), Matchers.is(Matchers.closeTo(5020.83, 0.01)));
		Assert.assertThat(installmentPayPlan.getInterest(), Matchers.is(Matchers.closeTo(20.83, 0.01)));
		Assert.assertThat(installmentPayPlan.getPrincipal(), Matchers.is(Matchers.closeTo(5000.00, 0.00001)));
		Assert.assertThat(installmentPayPlan.getRemainingOutstandingPrincipal(), Matchers.is(Matchers.closeTo(0.00, 0.0)));
		Assert.assertThat(installmentPayPlan.getInitialOutstandingPrincipal(), Matchers.is(Matchers.closeTo(5000.00, 0.0)));
	}
	
	
	@Test
	public void testgenerateLoanPlanForTwentyFourMonthDuration() {
		Loan loanDetail = new Loan();
		loanDetail.setDuration(24);
		loanDetail.setNominalRate(5.0d);
		loanDetail.setLoanAmount(5000.0);
		loanDetail.setStartDate(LocalDateTime.now());
		List<PaymentInfo> installmentPaymentPlans = planCalculator.generateLoanPlan(loanDetail);
		
		Assert.assertThat("verify the total installment duration", installmentPaymentPlans.size(), Matchers.is(loanDetail.getDuration()));
		
		Assert.assertThat("first installment ", installmentPaymentPlans.get(0).getBorrowerPaymentAmount(), Matchers.is(Matchers.closeTo(219.36, 0.01)));
		Assert.assertThat("first installment ", installmentPaymentPlans.get(0).getInitialOutstandingPrincipal(), Matchers.is(Matchers.closeTo(5000.00, 0.0)));
		Assert.assertThat("first installment ", installmentPaymentPlans.get(0).getInterest(), Matchers.is(Matchers.closeTo(20.83, 0.01)));
		Assert.assertThat("first installment ", installmentPaymentPlans.get(0).getPrincipal(), Matchers.is(Matchers.closeTo(198.52, 0.01)));
		Assert.assertThat("first installment ", installmentPaymentPlans.get(0).getRemainingOutstandingPrincipal(), Matchers.is(Matchers.closeTo(4801.48, 0.01)));
		
		PaymentInfo secondInstallmentPaymentPlan = planCalculator.createInstallmentPayInfo(LocalDateTime.now().plusMonths(1), 5.0d
				, installmentPaymentPlans.get(0).getRemainingOutstandingPrincipal(), loanDetail.getDuration()-1);
		
		Assert.assertThat("second installment ", installmentPaymentPlans.get(1).getBorrowerPaymentAmount(), Matchers.is(Matchers.closeTo(secondInstallmentPaymentPlan.getBorrowerPaymentAmount(), 0.01)));
		Assert.assertThat("second installment ", installmentPaymentPlans.get(1).getInitialOutstandingPrincipal(), Matchers.is(Matchers.closeTo(secondInstallmentPaymentPlan.getInitialOutstandingPrincipal(), 0.01)));
		Assert.assertThat("second installment ", installmentPaymentPlans.get(1).getInterest(), Matchers.is(Matchers.closeTo(secondInstallmentPaymentPlan.getInterest(), 0.01)));
		Assert.assertThat("second installment ", installmentPaymentPlans.get(1).getPrincipal(), Matchers.is(Matchers.closeTo(secondInstallmentPaymentPlan.getPrincipal(), 0.01)));
		Assert.assertThat("second installment ", installmentPaymentPlans.get(1).getRemainingOutstandingPrincipal(), Matchers.is(Matchers.closeTo(secondInstallmentPaymentPlan.getRemainingOutstandingPrincipal(), 0.01)));
		
	}
}
