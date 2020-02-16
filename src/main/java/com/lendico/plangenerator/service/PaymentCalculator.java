package com.lendico.plangenerator.service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.lendico.plangenerator.model.Loan;
import com.lendico.plangenerator.model.PaymentInfo;

@Service
public class PaymentCalculator {
	
	private static final int DAYS_IN_YEAR = 360;
	private static final int DAYS_IN_MONTH = 30;
	
	public List<PaymentInfo> generateLoanPlan(Loan loan) {
		List<PaymentInfo> paymentPlans = new ArrayList<>();
		NavigableMap<LocalDateTime, PaymentInfo> monthlyInstallmentMap = new TreeMap<>();
		int duration = loan.getDuration();
		double annualRate = loan.getNominalRate();
		
		while (monthlyInstallmentMap.size() != duration) {
			
			if(monthlyInstallmentMap.size() == 0) {
				double outstandingPrincipal = loan.getLoanAmount();
				LocalDateTime installmentDt = loan.getStartDate().with(TemporalAdjusters.firstDayOfMonth());
				PaymentInfo paymentInfo = createInstallmentPayInfo(installmentDt, annualRate, outstandingPrincipal, duration);
				monthlyInstallmentMap.put(installmentDt, paymentInfo);
				paymentPlans.add(paymentInfo);
			}else {
				
				double outstandingPrincipal = monthlyInstallmentMap.get(monthlyInstallmentMap.lastKey()).getRemainingOutstandingPrincipal();
				LocalDateTime nextInstallmentDt = monthlyInstallmentMap.lastKey().plusMonths(1);
				
				PaymentInfo paymentInfo = createInstallmentPayInfo(nextInstallmentDt, annualRate, outstandingPrincipal, duration - monthlyInstallmentMap.size());
				monthlyInstallmentMap.put(nextInstallmentDt, paymentInfo);
				paymentPlans.add(paymentInfo);
			}
		}
		return paymentPlans;
	}

	public double calculateInterest(double annualRate, double outstandingPrincipal) {

		return ((annualRate/100) * DAYS_IN_MONTH * outstandingPrincipal) / DAYS_IN_YEAR;
	}

	public double calculateAnnuity(double annualRate, double outstandingPrincipal, int duration) {
		double ratePerMonth = annualRate / 12;
		double divisor = 1 - Math.pow(1 + ratePerMonth / 100, -duration);
		return (ratePerMonth / 100 * outstandingPrincipal) / divisor;
	}

	public double calulatePrincipal(double annualRate, double outstandingPrincipal, int duration) {
		double annuity = calculateAnnuity(annualRate, outstandingPrincipal, duration);
		double interest = calculateInterest(annualRate, outstandingPrincipal);
		return annuity - interest;
	}

	public PaymentInfo createInstallmentPayInfo(LocalDateTime installmentDt, double annualRate, double outstandingPrincipal, int duration) {
		double annuaty = calculateAnnuity(annualRate, outstandingPrincipal, duration);
		double principal = calulatePrincipal(annualRate, outstandingPrincipal, duration);
		double calculateInterest = calculateInterest(annualRate, outstandingPrincipal);
		return PaymentInfo.builder()
				.date(installmentDt)
				.borrowerPaymentAmount(annuaty)
				.principal(principal)
				.interest(calculateInterest)
				.initialOutstandingPrincipal(outstandingPrincipal)
				.remainingOutstandingPrincipal(Math.max(0, (outstandingPrincipal - principal)))
				.build();
	}
}
