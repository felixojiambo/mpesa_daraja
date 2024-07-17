package com.tandapay.b2c.repository;

import documents.B2C_Entries;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface B2CEntriesRepository extends MongoRepository<B2C_Entries, String> {
    B2C_Entries findByConversationIdOrOriginatorConversationId(String conversationId, String originatorConversationId);
    B2C_Entries findByTransactionId(String transactionId);
}
