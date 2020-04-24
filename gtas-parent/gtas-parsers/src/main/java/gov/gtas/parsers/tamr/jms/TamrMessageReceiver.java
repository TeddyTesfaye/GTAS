/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.jms;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.parsers.tamr.TamrMessageHandlerService;
import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.parsers.tamr.model.TamrMessageType;

@Component
@ConditionalOnProperty(prefix = "tamr", name = "enabled")
public class TamrMessageReceiver {

	private final Logger logger = LoggerFactory.getLogger(TamrMessageReceiver.class);

    private TamrMessageHandlerService tamrMessageHandler;
	
    public TamrMessageReceiver(TamrMessageHandlerService tamrMessageHandler) {
        this.tamrMessageHandler = tamrMessageHandler;
    }
    
	@JmsListener(containerFactory = "tamrJmsListenerContainerFactory",
	        destination = "OutboundQueue")
	public void receive(javax.jms.Message msg) {
		logger.debug("############### Tamr data received .... ################");
		TextMessage textMessage = null;
		String jmsMessageType, messageText;
		TamrMessageType messageType;
		
		if (msg == null) {
		    logger.warn("Received null JMS message from Tamr");
		    return;
		}
		
		textMessage = (TextMessage) msg;
		try {
		    jmsMessageType = textMessage.getJMSType();
		    messageText = textMessage.getText();
		} catch (JMSException e) {
			logger.error("Error handling Tamr JMS message: {}",
			        e);
			return;
		}
        logger.debug("Message type: {}", jmsMessageType);
        if (messageText.length() > 1000)
            logger.debug("Raw JSON data: {}...", messageText.substring(0, 1000));
        else
            logger.debug("Raw JSON data: {}", messageText);

        TamrMessage response = this.parseMessage(messageText);
        if (response == null) {
            return;
        }
        try {
            messageType = TamrMessageType.fromString(jmsMessageType);
            response.setMessageType(messageType);
        } catch (IllegalArgumentException e) {
            // If the message type is invalid.
            logger.warn("Ignoring unknown Tamr JMS message type.", e);
            return;
        }
                
        if (messageType == TamrMessageType.QUERY) {
            tamrMessageHandler.handleQueryResponse(response);
        } else if (messageType == TamrMessageType.DC_REPLACE
                || messageType == TamrMessageType.TH_UPDATE) {
            // These responses should be an acknowledgement.
            tamrMessageHandler.handleAcknowledgeResponse(response);
        } else if (messageType == TamrMessageType.TH_CLUSTERS
                || messageType == TamrMessageType.TH_DELTAS) {
            tamrMessageHandler.handleTamrIdUpdate(response);
        } else if (messageType == TamrMessageType.ERROR) {
            tamrMessageHandler.handleErrorResponse(response);
        }
            
	}

	private TamrMessage parseMessage(String messageJson) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(messageJson, TamrMessage.class);
        } catch (JsonParseException e) {
            logger.error("Error parsing JSON from Tamr: {}",
                    e.getMessage());
            return null;
        } catch (JsonMappingException e) {
            logger.error("Error deserializing JSON from Tamr: {}",
                    e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(e.toString());
            return null;
        }
	}
}
