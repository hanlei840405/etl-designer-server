package com.nxin.framework.etl.designer.converter.designer.transform.lookup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.converter.designer.transform.TransformConvertChain;
import com.nxin.framework.etl.designer.enums.Constant;
import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.rest.RestMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "RestMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            List<String> headerFields = new ArrayList<>(0);
            List<String> headerNames = new ArrayList<>(0);
            RestMeta restMeta = new RestMeta();
            String stepName = (String) formAttributes.get("name");
            String url = (String) formAttributes.get("url");
            boolean urlInStream = (boolean) formAttributes.get("urlInStream");
            String urlField = (String) formAttributes.get("urlField");
            String method = (String) formAttributes.get("method");
            boolean methodInStream = (boolean) formAttributes.get("methodInStream");
            String methodField = (String) formAttributes.get("methodField");
            String body = (String) formAttributes.get("body");
            String applicationType = (String) formAttributes.get("applicationType");
            String resultFieldName = (String) formAttributes.get("resultFieldName");
            String statusFieldName = (String) formAttributes.get("statusFieldName");
            String costTimeField = (String) formAttributes.get("costTimeField");
            String responseHeaderField = (String) formAttributes.get("responseHeaderField");
            String login = (String) formAttributes.get("login");
            String password = (String) formAttributes.get("password");
            boolean preemptive = (boolean) formAttributes.get("preemptive");
            String proxyHost = (String) formAttributes.get("proxyHost");
            String proxyPort = (String) formAttributes.get("proxyPort");
            restMeta.setUrl(url);
            restMeta.setUrlInField(urlInStream);
            restMeta.setUrlField(urlField);
            restMeta.setMethod(method);
            restMeta.setDynamicMethod(methodInStream);
            restMeta.setMethodFieldName(methodField);
            restMeta.setBodyField(body);
            restMeta.setApplicationType(applicationType);
            restMeta.setFieldName(resultFieldName);
            restMeta.setResultCodeFieldName(statusFieldName);
            restMeta.setResponseTimeFieldName(costTimeField);
            restMeta.setResponseHeaderFieldName(responseHeaderField);
            restMeta.setHttpLogin(login);
            restMeta.setHttpPassword(password);
            restMeta.setPreemptive(preemptive);
            restMeta.setProxyHost(proxyHost);
            restMeta.setProxyPort(proxyPort);
            List<Map<String, String>> headers = (List<Map<String, String>>) formAttributes.get("headers");
            for (Map<String, String> header : headers) {
                headerFields.add(header.get("field"));
                headerNames.add(header.get("name"));
            }
            restMeta.setHeaderField(headerFields.toArray(new String[0]));
            restMeta.setHeaderName(headerNames.toArray(new String[0]));
            StepMeta stepMeta = new StepMeta(stepName, restMeta);
            if (formAttributes.containsKey("distribute")) {
                boolean distribute = (boolean) formAttributes.get("distribute");
                stepMeta.setDistributes(distribute);
            }
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            stepMeta.setCopies(parallel);
            return new ResponseMeta(cell.getId(), stepMeta, null);
        } else {
            return next.parse(cell, transMeta);
        }
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
