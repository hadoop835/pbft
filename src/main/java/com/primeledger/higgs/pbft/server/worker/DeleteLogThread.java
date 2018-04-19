package com.primeledger.higgs.pbft.server.worker;

import com.primeledger.higgs.pbft.server.log.ILogState;

public class DeleteLogThread extends Thread {

    private ILogState iLogState = null;

    private int start;
    private int end;

    public DeleteLogThread(ILogState iLogState, int start, int end) {
        this.iLogState = iLogState;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        System.out.println("start delete log from " + start + " to " + end);
        iLogState.deleteLog(start, end);
    }
}
