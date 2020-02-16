package com.lendico.plangenerator.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendico.plangenerator.LendicoLoanPlanGeneratorApplication;
import com.lendico.plangenerator.model.Loan;
import com.lendico.plangenerator.model.PaymentInfo;
import com.lendico.plangenerator.util.LendicoUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LendicoLoanPlanGeneratorApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "test" })
public class TestPlanController {
	
	@LocalServerPort
	int randomServerPort;
	
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Value("classpath:data_json.json")
	private Resource resourceFile;
	
	@Autowired
	private ObjectMapper mapper;
	
	private static PaymentInfo[] mockedPaymentPlanInfo = null;
	
	@Before
	public  void setup() {
		if(null == mockedPaymentPlanInfo) {
			loadJson();
		}
	}
	private void loadJson() {
		try{
			mockedPaymentPlanInfo = mapper.readValue(resourceFile.getFile(), PaymentInfo[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPostHttpCallGeneratePlan() {
		Loan loan = new Loan();
		loan.setDuration(24);
		loan.setNominalRate(5.0d);
		loan.setLoanAmount(5000.0);
		loan.setStartDate(LocalDateTime.now());
		ResponseEntity<List<PaymentInfo>> paymentPlanResponse = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/plans/generate-plan").buildAndExpand(new HashMap<>()).toUri()
				, HttpMethod.POST
				, LendicoUtil.getEnityWithHttpHeader(loan)
				, new ParameterizedTypeReference<List<PaymentInfo>>() {
				});
		
		List<PaymentInfo> paymentPlanDetails = paymentPlanResponse.getBody();
		
		Assert.assertThat(paymentPlanResponse.getStatusCode(), Matchers.isIn(Arrays.asList(HttpStatus.OK, HttpStatus.CREATED)));
		Assert.assertEquals("varify the installment duration", 24, paymentPlanDetails.size());
		
		PaymentInfo valideFithInstallment = mockedPaymentPlanInfo[4];
		Assert.assertThat("validate fifth installment ", paymentPlanDetails.get(4).getBorrowerPaymentAmount(), Matchers.is(Matchers.closeTo(valideFithInstallment .getBorrowerPaymentAmount(), 0.01)));
		Assert.assertThat("validate fifth installment ", paymentPlanDetails.get(4).getInitialOutstandingPrincipal(), Matchers.is(Matchers.closeTo(valideFithInstallment.getInitialOutstandingPrincipal(), 0.01)));
		Assert.assertThat("validate fifth installment ", paymentPlanDetails.get(4).getInterest(), Matchers.is(Matchers.closeTo(valideFithInstallment.getInterest(), 0.01)));
		Assert.assertThat("validate fifth installment ", paymentPlanDetails.get(4).getPrincipal(), Matchers.is(Matchers.closeTo(valideFithInstallment.getPrincipal(), 0.01)));
		Assert.assertThat("validate fifth installment ", paymentPlanDetails.get(4).getRemainingOutstandingPrincipal(), Matchers.is(Matchers.closeTo(valideFithInstallment.getRemainingOutstandingPrincipal(), 0.01)));
		
		
		PaymentInfo validatelastInstallment = mockedPaymentPlanInfo[paymentPlanDetails.size()-1];
		Assert.assertThat("validate last installment ", paymentPlanDetails.get(paymentPlanDetails.size()-1).getBorrowerPaymentAmount(), Matchers.is(Matchers.closeTo(validatelastInstallment .getBorrowerPaymentAmount(), 0.01)));
		Assert.assertThat("validate last installment ", paymentPlanDetails.get(paymentPlanDetails.size()-1).getInitialOutstandingPrincipal(), Matchers.is(Matchers.closeTo(validatelastInstallment.getInitialOutstandingPrincipal(), 0.01)));
		Assert.assertThat("validate last installment ", paymentPlanDetails.get(paymentPlanDetails.size()-1).getInterest(), Matchers.is(Matchers.closeTo(validatelastInstallment.getInterest(), 0.01)));
		Assert.assertThat("validate last installment ", paymentPlanDetails.get(paymentPlanDetails.size()-1).getPrincipal(), Matchers.is(Matchers.closeTo(validatelastInstallment.getPrincipal(), 0.01)));
		Assert.assertThat("validate last installment ", paymentPlanDetails.get(paymentPlanDetails.size()-1).getRemainingOutstandingPrincipal(), Matchers.is(Matchers.closeTo(validatelastInstallment.getRemainingOutstandingPrincipal(), 0.01)));
		
	}
	
	@Test
	public void testPostHttpCallBadRequestDurationIsSetAsZero() {
		Loan loan = new Loan();
		loan.setDuration(0);
		loan.setNominalRate(5.0d);
		loan.setLoanAmount(5000.0);
		loan.setStartDate(LocalDateTime.now());
		ResponseEntity<?> response = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/plans/generate-plan").buildAndExpand(new HashMap<>()).toUri()
				, HttpMethod.POST
				, LendicoUtil.getEnityWithHttpHeader(loan)
				, Object.class);
		
		
		Assert.assertThat(response.getStatusCode(), Matchers.isIn(Arrays.asList(HttpStatus.BAD_REQUEST)));
		Assert.assertThat(response.getBody().toString(), Matchers.is(Matchers.containsString("field=duration")));
		Assert.assertThat(response.getBody().toString(), Matchers.is(Matchers.containsString("message=must be greater than 0")));
	}
	
	@Test
	public void testPostHttpCallBadRequestLoanAmountIsZero() {
		Loan loan = new Loan();
		loan.setDuration(24);
		loan.setNominalRate(5.0d);
		loan.setLoanAmount(0);
		loan.setStartDate(LocalDateTime.now());
		ResponseEntity<?> response = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/plans/generate-plan").buildAndExpand(new HashMap<>()).toUri()
				, HttpMethod.POST
				, LendicoUtil.getEnityWithHttpHeader(loan)
				, Object.class);
		
		
		Assert.assertThat(response.getStatusCode(), Matchers.isIn(Arrays.asList(HttpStatus.BAD_REQUEST)));
		Assert.assertThat(response.getBody().toString(), Matchers.is(Matchers.containsString("field=loanAmount")));
		Assert.assertThat(response.getBody().toString(), Matchers.is(Matchers.containsString("message=must be greater than 0")));
	}
}
