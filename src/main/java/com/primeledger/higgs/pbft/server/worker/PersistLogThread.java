package com.primeledger.higgs.pbft.server.worker;

import com.primeledger.higgs.pbft.common.message.MessageType;
import com.primeledger.higgs.pbft.common.message.RecoverMessage;
import com.primeledger.higgs.pbft.common.message.StateLog;
import com.primeledger.higgs.pbft.server.main.ServerViewController;
import com.primeledger.higgs.pbft.server.communication.MessageBroadcaster;
import com.primeledger.higgs.pbft.server.recover.IRecoverable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PersistLogThread extends Thread {

    /**
     * persis log queue
     */
    private BlockingQueue<StateLog> persistQueue;

    private boolean doWork = true;

    private ServerViewController controller = null;

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private MessageBroadcaster messageBroadcaster = null;

    private IRecoverable recoverable = null;


    public PersistLogThread(BlockingQueue<StateLog> persistQueue, ServerViewController controller, MessageBroadcaster messageBroadcaster, IRecoverable recoverable) {

        this.persistQueue = persistQueue;
        this.controller = controller;
        this.messageBroadcaster = messageBroadcaster;
        this.recoverable = recoverable;
    }

    @Override
    public void run() {
        while (doWork) {
            try {
                StateLog stateLog = persistQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (stateLog == null) continue;
                if (stateLog.getCp() <= controller.getStableCp()) continue;

                if (stateLog.getCp() != controller.getStableCp() + 1) {
                    //ask sync log
                    System.out.println("the check point is not sync,now stable check point is:" + controller.getStableCp() + "the state log's check point is:" + stateLog.getCp());
                    RecoverMessage recoverMessage = new RecoverMessage();
                    recoverMessage.setEndCp(stateLog.getCp() - 1);
                    recoverMessage.setType(MessageType.ASK_SYN_LOG);
                    recoverMessage.setStartCp(controller.getStableCp() + 1);
                    recoverMessage.setSender(controller.getMyId());
                    messageBroadcaster.send(controller.getCurrentLeader(), recoverMessage);
                    lock.lock();
                    condition.await(15000, TimeUnit.MILLISECONDS);
                    lock.unlock();
                }
                if (stateLog.getCp() == controller.getStableCp() + 1) {
                    recoverable.persistLog(stateLog);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }

    public void haveDoneSyncLog() {
        condition.signal();
    }
}
