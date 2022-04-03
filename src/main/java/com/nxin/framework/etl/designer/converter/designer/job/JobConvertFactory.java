package com.nxin.framework.etl.designer.converter.designer.job;

import com.nxin.framework.etl.designer.converter.designer.ConvertFactory;
import com.nxin.framework.etl.designer.converter.designer.job.common.*;
import com.nxin.framework.etl.designer.converter.designer.job.condition.JobEntrySimpleEvalChain;
import com.nxin.framework.etl.designer.converter.designer.job.shell.JobEntryEvalChain;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import org.pentaho.di.core.exception.KettleException;

public class JobConvertFactory extends ConvertFactory {
    private static JobConvertChain beginChain;

    public static void init(DatasourceService datasourceService, ShellService shellService) {
        JobConvertChain beginChain = new BeginChain();
        JobConvertChain jobEntrySpecialChain = new JobEntrySpecialChain();
        JobConvertChain jobEntryDummyChain = new JobEntryDummyChain();
        JobConvertChain jobEntryTransChain = new JobEntryTransChain();
        JobConvertChain jobEntryJobChain = new JobEntryJobChain();
        JobConvertChain jobEntrySuccessChain = new JobEntrySuccessChain();
        JobConvertChain jobEntrySetVariablesChain = new JobEntrySetVariablesChain();
        JobConvertChain jobEntryEvalChain = new JobEntryEvalChain();
        JobConvertChain jobEntrySimpleEvalChain = new JobEntrySimpleEvalChain();
        JobConvertChain jobHopChain = new JobHopChain();
        JobConvertChain endChain = new EndChain();
        jobEntryTransChain.setShellService(shellService);
        beginChain.setNext(jobEntrySpecialChain);
        jobEntrySpecialChain.setNext(jobEntryDummyChain);
        jobEntryDummyChain.setNext(jobEntryTransChain);
        jobEntryTransChain.setNext(jobEntryJobChain);
        jobEntryJobChain.setNext(jobEntrySuccessChain);
        jobEntrySuccessChain.setNext(jobEntrySetVariablesChain);
        jobEntrySetVariablesChain.setNext(jobEntryEvalChain);
        jobEntryEvalChain.setNext(jobEntrySimpleEvalChain);
        jobEntrySimpleEvalChain.setNext(jobHopChain);
        jobHopChain.setNext(endChain);
        JobConvertFactory.beginChain = beginChain;
    }


    public static JobConvertChain getInstance() throws KettleException {
        return beginChain;
    }
}
