package documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Document(collection = "b2c_entries")
@Data
public class B2C_Entries {
    @Id
    private String internalId;

    private String transactionType;

    @Indexed(unique = true)
    private String transactionId;

    private String msisdn;

    private Long amount;

    @Indexed(unique = true)
    private String conversationId;
    private String billRefNumber;
    @Indexed(unique = true)
    private String originatorConversationId;

    private Date EntryDate;

    private String resultCode;

    private Object rawCallbackPayloadResponse;
}