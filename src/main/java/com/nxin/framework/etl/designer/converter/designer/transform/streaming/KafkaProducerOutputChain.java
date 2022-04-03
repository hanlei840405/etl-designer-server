package com.nxin.framework.etl.designer.converter.designer.transform.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.big.data.kettle.plugins.kafka.KafkaProducerOutputMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class KafkaProducerOutputChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "KafkaProducerOutputMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            KafkaProducerOutputMeta kafkaProducerOutputMeta = new KafkaProducerOutputMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String stepName = (String) formAttributes.get("name");
            String servers = (String) formAttributes.get("servers");
            String clientId = (String) formAttributes.get("clientId");
            String topic = (String) formAttributes.get("topic");
            String keyField = (String) formAttributes.get("keyField");
            String messageField = (String) formAttributes.get("messageField");
            List<Map<String, String>> options = (List<Map<String, String>>) formAttributes.get("options");
            Map<String, String> config = new HashMap<>(0);
            for (Map<String, String> option : options) {
                String v = "";
                if (StringUtils.hasLength(option.get("value"))) {
                    v = option.get("value");
                }
                config.put(option.get("name"), v);
            }
            kafkaProducerOutputMeta.setDirectBootstrapServers(servers);
            kafkaProducerOutputMeta.setClientId(clientId);
            kafkaProducerOutputMeta.setConnectionType(KafkaProducerOutputMeta.ConnectionType.DIRECT);
            kafkaProducerOutputMeta.setKeyField(keyField);
            kafkaProducerOutputMeta.setMessageField(messageField);
            kafkaProducerOutputMeta.setTopic(topic);
            kafkaProducerOutputMeta.setConfig(config);

            StepMeta stepMeta = new StepMeta(stepName, kafkaProducerOutputMeta);
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
