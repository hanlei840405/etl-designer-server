package com.nxin.framework.etl.designer.converter.designer.job;

import com.mxgraph.model.mxCell;
import com.nxin.framework.etl.designer.converter.designer.transform.ResponseMeta;
import org.pentaho.di.job.JobMeta;

import java.io.IOException;

public class EndChain extends JobConvertChain {

    @Override
    public ResponseMeta parse(mxCell cell, JobMeta jobMeta) throws IOException {
        return null;
    }

    @Override
    public void callback(JobMeta jobMeta) {

    }
}
