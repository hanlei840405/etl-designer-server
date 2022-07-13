package com.nxin.framework.etl.designer.converter.designer.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TransformConvertChain {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected TransformConvertChain next;

    protected DatasourceService datasourceService;

    private ShellService shellService;

    public void setNext(TransformConvertChain next) {
        this.next = next;
    }

    public DatasourceService getDatasourceService() {
        return datasourceService;
    }

    public void setDatasourceService(DatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }

    public ShellService getShellService() {
        return shellService;
    }

    public void setShellService(ShellService shellService) {
        this.shellService = shellService;
    }

    public abstract ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException;

    public abstract void callback(TransMeta transMeta, Map<String, String> idNameMapping);
}
