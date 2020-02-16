package com.lendico.plangenerator.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lendico.plangenerator.model.Loan;
import com.lendico.plangenerator.model.PaymentInfo;
import com.lendico.plangenerator.service.PaymentCalculator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/plans", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api("Operations on loan plan ")
@Validated
@CrossOrigin
public class PlanController {
	
	@Autowired
	private PaymentCalculator paymentCalculator;
	
	@ApiOperation(value = "generate payment plan", response=PaymentInfo.class, responseContainer = "list")
	@PostMapping("/generate-plan")
	public ResponseEntity<List<PaymentInfo>> genareLoanPlan(@Valid @RequestBody Loan loanDetail) {
		List<PaymentInfo> planDetails = paymentCalculator.generateLoanPlan(loanDetail);
		return new ResponseEntity<>(planDetails, HttpStatus.OK);
	}
}
