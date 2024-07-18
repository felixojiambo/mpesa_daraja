package com.tandapay.b2c.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommonSyncResponse {

	@JsonProperty("ConversationID")
	private String conversationID;

	@JsonProperty("OriginatorConversationID")
	private String originatorConversationID;

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("ResponseDescription")
	private String responseDescription;

	@JsonProperty("RequestID")
	private String requestID;

	@JsonProperty("ErrorCode")
	private String errorCode;

	@JsonProperty("ErrorMessage")
	private String errorMessage;
}
