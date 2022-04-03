package com.nxin.framework.etl.designer.converter.designer.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mxgraph.model.mxCell;
import org.pentaho.di.trans.TransMeta;

import java.util.Map;

public class BeginChain extends TransformConvertChain {
    @Override
    public ResponseMeta parse(mxCell cell, TransMeta transMeta) throws JsonProcessingException {
        return next.parse(cell, transMeta);
    }

    @Override
    public void callback(TransMeta transMeta, Map<String, String> idNameMapping) {

    }
}
