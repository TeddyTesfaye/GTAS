package gov.gtas.parsers.tamr.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import gov.gtas.parsers.tamr.model.TamrMessageType;
import gov.gtas.parsers.tamr.jms.TamrQueueConfig;

@Component
@ConditionalOnProperty(prefix = "tamr", name = "enabled")
public class TamrMessageSender {

    private final Logger logger = LoggerFactory.getLogger(TamrMessageSender.class);

    JmsTemplate jmsTemplate;

    TamrQueueConfig queueConfig;
    
    public TamrMessageSender(TamrQueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }
    
    public void sendMessageToTamr(TamrMessageType messageType, Object messageObject) {
        String messageJson;
        try {
            ObjectMapper mapper = new ObjectMapper();
            messageJson = mapper.writer().writeValueAsString(messageObject);
        } catch (JsonProcessingException e) {
            logger.error("Could not send {} message (type={}) to Tamr: {}",
                    messageObject.getClass(), messageType, e);
            return;
        }
        this.sendTextMessageToTamr(messageType, messageJson);
    }
    
    public void sendTextMessageToTamr(
            TamrMessageType messageType, String messageText) {
        if (jmsTemplate == null) {
            this.jmsTemplate = new JmsTemplate(
                    queueConfig.senderConnectionFactory());
        }
        logger.info("Sending {} message to Tamr.", messageType);
        logger.debug(messageText);
 
        jmsTemplate.send("InboundQueue", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(messageText);
                message.setJMSType(messageType.toString());
                return message;
            }
        });
    }

}
