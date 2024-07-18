package com.tandapay.b2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalLNMRequest{

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;
}