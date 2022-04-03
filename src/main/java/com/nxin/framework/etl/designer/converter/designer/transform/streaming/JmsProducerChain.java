package com.nxin.framework.etl.designer.converter.designer.transform.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.jms.JmsProducerMeta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.pentaho.di.trans.step.jms.JmsProducerMeta.*;

@Slf4j
public class JmsProducerChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "JmsProducerMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            JmsProducerMeta jmsProducerMeta = new JmsProducerMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String connection = (String) formAttributes.get("connection");
            String url = (String) formAttributes.get("url");
            String destinationType = (String) formAttributes.get("destinationType");
            String destinationName = (String) formAttributes.get("destinationName");
            String messageField = (String) formAttributes.get("messageField");
            String username = (String) formAttributes.get("username");
            String password = (String) formAttributes.get("password");
            List<Map<String, String>> options = (List<Map<String, String>>) formAttributes.get("options");
            for (Map<String, String> option : options) {
                if (DISABLE_MESSAGE_ID.equals(option.get("name"))) {
                    jmsProducerMeta.setDisableMessageId(option.get("value"));
                } else if (DISABLE_MESSAGE_TIMESTAMP.equals(option.get("name"))) {
                    jmsProducerMeta.setDisableMessageTimestamp(option.get("value"));
                } else if (DELIVERY_MODE.equals(option.get("name"))) {
                    jmsProducerMeta.setDeliveryMode(option.get("value"));
                } else if (PRIORITY.equals(option.get("name"))) {
                    jmsProducerMeta.setPriority(option.get("value"));
                } else if (TIME_TO_LIVE.equals(option.get("name"))) {
                    jmsProducerMeta.setTimeToLive(option.get("value"));
                } else if (DELIVERY_DELAY.equals(option.get("name"))) {
                    jmsProducerMeta.setDeliveryDelay(option.get("value"));
                } else if (JMS_CORRELATION_ID.equals(option.get("name"))) {
                    jmsProducerMeta.setJmsCorrelationId(option.get("value"));
                } else if (JMS_TYPE.equals(option.get("name"))) {
                    jmsProducerMeta.setJmsType(option.get("value"));
                }
            }
            List<Map<String, String>> parameters = (List<Map<String, String>>) formAttributes.get("parameters");

            Map<String, String> propertyValuesByName = new LinkedHashMap<>();

            for (Map<String, String> parameter : parameters) {
                String propertyName = parameter.get("name");
                String propertyValue = parameter.get("value");
                if (!StringUtils.isBlank(propertyName) && !propertyValuesByName.containsKey(propertyName)) {
                    propertyValuesByName.put(propertyName, propertyValue);
                }
            }
            jmsProducerMeta.jmsDelegate.connectionType = connection;
            jmsProducerMeta.jmsDelegate.destinationType = destinationType;
            jmsProducerMeta.jmsDelegate.destinationName = destinationName;
            jmsProducerMeta.setFieldToSend(messageField);
            if ("ACTIVEMQ".equals(connection)) {
                jmsProducerMeta.jmsDelegate.amqUsername = username;
                jmsProducerMeta.jmsDelegate.amqPassword = password;
                jmsProducerMeta.jmsDelegate.amqUrl = url;
            } else {
                jmsProducerMeta.jmsDelegate.ibmUsername = username;
                jmsProducerMeta.jmsDelegate.ibmUsername = password;
                jmsProducerMeta.jmsDelegate.ibmUrl = url;
            }
            jmsProducerMeta.setPropertyValuesByName(propertyValuesByName);
            StepMeta stepMeta = new StepMeta(stepName, jmsProducerMeta);
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
