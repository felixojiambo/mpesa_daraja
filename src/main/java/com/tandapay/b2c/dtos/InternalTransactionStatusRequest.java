package com.tandapay.b2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InternalTransactionStatusRequest {

	@JsonProperty("TransactionID")
	private String transactionID;
}