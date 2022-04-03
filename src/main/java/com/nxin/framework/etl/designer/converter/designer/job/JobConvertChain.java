package com.nxin.framework.etl.designer.converter.designer.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import org.pentaho.di.job.JobMeta;

import java.io.IOException;

public abstract class JobConvertChain {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected mxGraph graph = new mxGraph();

    protected JobConvertChain next;

    protected DatasourceService datasourceService;

    private ShellService shellService;

    public void setNext(JobConvertChain next) {
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

    /**
     * mxgraph生成kettle组件
     *
     * @param cell
     * @param jobMeta
     * @return
     * @throws IOException
     */
    public abstract ResponseMeta parse(mxCell cell, JobMeta jobMeta) throws IOException;

    public abstract void callback(JobMeta jobMeta);
}
