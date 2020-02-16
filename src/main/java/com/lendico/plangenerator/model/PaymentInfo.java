package com.lendico.plangenerator.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lendico.plangenerator.json.serializer.DoubleContextualSerializer;
import com.lendico.plangenerator.json.serializer.Precision;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentInfo implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	
	@JsonSerialize(using = DoubleContextualSerializer .class)
	@Precision(precision = 2)
	private double borrowerPaymentAmount;
	
	private LocalDateTime date;
	
	@JsonSerialize(using = DoubleContextualSerializer .class)
	@Precision(precision = 2)
	private double initialOutstandingPrincipal;
	
	@JsonSerialize(using = DoubleContextualSerializer .class)
	@Precision(precision = 2)
	private double interest;
	
	@JsonSerialize(using = DoubleContextualSerializer .class)
	@Precision(precision = 2)
	private double principal;
	
	@JsonSerialize(using = DoubleContextualSerializer .class)
	@Precision(precision = 2)
	private double remainingOutstandingPrincipal;
	
}
