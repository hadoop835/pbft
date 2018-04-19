package com.primeledger.higgs.pbft.server.log;

import com.primeledger.higgs.pbft.common.message.StateLog;

public interface ILogState {
    /**
     * this function is to get the stable check point
     * @return initial check point
     */
    int getStableCp();

    /**
     * this function is used to save the stable check point
     * @param cp
     */
    void putStableCp(int cp);

    /**
     * this function is to save the execute log
     * @param stateLog
     */
    void putLog(StateLog stateLog);

    /**
     * this function is to get a piece of state log
     * @param start
     * @param end
     * @return
     */
    StateLog[] getStateLog(int start, int end);

    /**
     * this function is used to get the lowest check point
     * so we can delete the log before this check point
     * @return the lowest stable check point
     */
    int getLowStableCp();

    /**
     * when we delete the log before the check point
     * we should update the lowest stable check point
     * @param cp
     */
    void setLowStableCp(int cp);

    /**
     * delete the log between start and end
     * @param start
     * @param end
     */
    void deleteLog(int start,int end);
}
