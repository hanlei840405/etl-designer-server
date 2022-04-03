package com.nxin.framework.etl.designer.converter.designer.transform.process;

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
import org.pentaho.di.trans.steps.mail.MailMeta;

import java.util.Map;

@Slf4j
public class MailChain extends TransformConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        if (cell.isVertex() && "MailMeta".equalsIgnoreCase(cell.getStyle())) {
            DeferredElementImpl value = (DeferredElementImpl) cell.getValue();
            MailMeta mailMeta = new MailMeta();
            Map<String, Object> formAttributes = objectMapper.readValue(value.getAttribute("form"), new TypeReference<Map<String, Object>>() {
            });
            String to = (String) formAttributes.get("to");
            String cc = (String) formAttributes.get("cc");
            String bcc = (String) formAttributes.get("bcc");
            String fromName = (String) formAttributes.get("fromName");
            String fromMail = (String) formAttributes.get("fromMail");
            String replyTo = (String) formAttributes.get("replyTo");
            String contact = (String) formAttributes.get("contact");
            String phoneNo = (String) formAttributes.get("phoneNo");
            String smtp = (String) formAttributes.get("smtp");
            String port = (String) formAttributes.get("port");
            String username = (String) formAttributes.get("username");
            String password = (String) formAttributes.get("password");
            boolean useAuth = (boolean) formAttributes.get("useAuth");
            boolean useSafeMode = (boolean) formAttributes.get("useSafeMode");
            String safeMode = (String) formAttributes.get("safeMode");
            boolean useDate = (boolean) formAttributes.get("useDate");
            boolean onlyText = (boolean) formAttributes.get("onlyText");
            boolean useHtml = (boolean) formAttributes.get("useHtml");
            boolean usePriority = (boolean) formAttributes.get("usePriority");
            String priority = (String) formAttributes.get("priority");
            String important = (String) formAttributes.get("important");
            String sensitivity = (String) formAttributes.get("sensitivity");
            String subject = (String) formAttributes.get("subject");
            String text = (String) formAttributes.get("text");
            int parallel = (int) formAttributes.get(Constant.ETL_PARALLEL);
            mailMeta.setDestination(to);
            mailMeta.setDestinationCc(cc);
            mailMeta.setDestinationBCc(bcc);
            mailMeta.setReplyAddress(fromMail);
            mailMeta.setReplyName(fromName);
            mailMeta.setReplyToAddresses(replyTo);
            mailMeta.setContactPerson(contact);
            mailMeta.setContactPhone(phoneNo);
            mailMeta.setServer(smtp);
            mailMeta.setPort(port);
            mailMeta.setUsingAuthentication(useAuth);
            mailMeta.setAuthenticationUser(username);
            mailMeta.setAuthenticationPassword(password);
            mailMeta.setUsingSecureAuthentication(useSafeMode);
            mailMeta.setSecureConnectionType(safeMode);
            mailMeta.setIncludeDate(useDate);
            mailMeta.setOnlySendComment(onlyText);
            mailMeta.setUseHTML(useHtml);
            mailMeta.setUsePriority(usePriority);
            mailMeta.setPriority(priority);
            mailMeta.setImportance(important);
            mailMeta.setSensitivity(sensitivity);
            mailMeta.setSubject(subject);
            mailMeta.setComment(text);
            mailMeta.setEncoding("UTF-8");
            mailMeta.setContentIds(new String[0]);
            mailMeta.setEmbeddedImages(new String[0]);
            String stepName = (String) formAttributes.get("name");
            StepMeta stepMeta = new StepMeta(stepName, mailMeta);
            mxGeometry geometry = cell.getGeometry();
            stepMeta.setLocation(new Double(geometry.getX()).intValue(), new Double(geometry.getY()).intValue());
            stepMeta.setDraw(true);
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
