package com.primeledger.higgs.pbft.server.recover;

import com.primeledger.higgs.pbft.common.message.RecoverMessage;
import com.primeledger.higgs.pbft.common.message.StateLog;

public interface IRecoverable {
    /**
     * if this node didn't catch up the current log
     * it will recover from other node by sync the log
     * @param recoverMessage
     * @return
     */
    boolean recover(RecoverMessage recoverMessage);

    /**
     * if this consensus success,it will persist the state log
     * @param stateLog
     * @return
     */
    boolean persistLog(StateLog stateLog);

}
