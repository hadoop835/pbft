package com.primeledger.higgs.pbft.server.main;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.message.MessageType;
import com.primeledger.higgs.pbft.common.message.RecoverMessage;
import com.primeledger.higgs.pbft.common.message.StateLog;
import com.primeledger.higgs.pbft.common.network.api.ICommitConsensus;
import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.server.communication.ServerCommunicationSystem;
import com.primeledger.higgs.pbft.server.consensus.ConsensusManager;
import com.primeledger.higgs.pbft.server.consensus.ConsensusResolver;
import com.primeledger.higgs.pbft.server.log.ILogState;
import com.primeledger.higgs.pbft.server.log.LogToLevelDB;
import com.primeledger.higgs.pbft.server.recover.IRecoverable;
import com.primeledger.higgs.pbft.server.worker.ClientMessagerResolver;
import com.primeledger.higgs.pbft.server.worker.DeleteLogThread;
import com.primeledger.higgs.pbft.server.worker.PersistLogThread;
import com.primeledger.higgs.pbft.server.worker.SendThread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Replica implements IRecoverable {

    private LinkedBlockingQueue<BaseMessage> inQueue = null;

    private LinkedBlockingQueue<BaseMessage> reuqestQueue = null;

    private LinkedBlockingQueue<BaseMessage> outQueue = null;

    private LinkedBlockingQueue<StateLog> persistQueue = null;

    private ICommitConsensus commitConsensus = null;

    private ServerViewController controller = null;

    private ILogState logState = null;

    private ServerCommunicationSystem scs = null;

    private ReentrantLock recoverLock = new ReentrantLock();

    private Condition recoverCondtion = recoverLock.newCondition();


    public Replica(Config config) {
        controller = new ServerViewController(config);
        inQueue = new LinkedBlockingQueue<>(config.getInQueueSize());
        reuqestQueue = new LinkedBlockingQueue<>(config.getRequestQueueSize());
        outQueue = new LinkedBlockingQueue<>(config.getInQueueSize());
        persistQueue = new LinkedBlockingQueue<>(config.getInQueueSize());
        logState = new LogToLevelDB(config.getLogPath());

        controller.initStableCp(logState.getStableCp());
        controller.setLowStableCp(logState.getLowStableCp());


        scs = new ServerCommunicationSystem(config, inQueue, reuqestQueue, this);

        ConsensusManager consensusManager = new ConsensusManager(controller);

        ClientMessagerResolver clientMessagerResolver = new ClientMessagerResolver(consensusManager, outQueue, reuqestQueue, controller);
        clientMessagerResolver.start();

        ConsensusResolver resolver = new ConsensusResolver(consensusManager, controller, inQueue, scs, persistQueue);
        resolver.start();

        SendThread sendThread = new SendThread(outQueue, controller, scs);
        sendThread.start();

        PersistLogThread persistLogThread = new PersistLogThread(persistQueue, controller, scs, this,recoverLock,recoverCondtion);
        persistLogThread.start();


    }


    public void setCommitConsensus(ICommitConsensus commitConsensus) {
        this.commitConsensus = commitConsensus;
    }


    @Override
    public boolean recover(RecoverMessage recoverMessage) {
        if (recoverMessage.getType() == MessageType.ASK_SYN_LOG) {
            StateLog[] stateLogs = logState.getStateLog(recoverMessage.getStartCp(), recoverMessage.getEndCp());
            RecoverMessage backSyncLog = new RecoverMessage();
            backSyncLog.setType(MessageType.BACK_SYC_LOG);
            backSyncLog.setStartCp(recoverMessage.getStartCp());
            backSyncLog.setEndCp(recoverMessage.getEndCp());
            backSyncLog.setStates(stateLogs);

            scs.send(recoverMessage.getSender(), backSyncLog);
        } else if (recoverMessage.getType() == MessageType.BACK_SYC_LOG) {
            StateLog[] stateLogs = recoverMessage.getStates();
            for (StateLog stateLog : stateLogs) {
                if (stateLog.getCp() == 1 + controller.getStableCp() && commitConsensus.commit(stateLog.getOperation())) {
                    logState.putLog(stateLog);
                    logState.putStableCp(controller.incStableCp());
                }
            }
            recoverLock.lock();
            recoverCondtion.signal();
            recoverLock.unlock();
        }

        return false;
    }

    @Override
    public boolean persistLog(StateLog stateLog) {
        if (commitConsensus.commit(stateLog.getOperation())) {
            logState.putLog(stateLog);
            logState.putStableCp(controller.incStableCp());
            if (controller.getStableCp() - controller.getLowStableCp() > 2000) {
                new DeleteLogThread(logState, controller.getLowStableCp(), controller.getLowStableCp() + 999).start();
                controller.setLowStableCp(controller.getLowStableCp() + 1000);
                logState.setLowStableCp(controller.getLowStableCp());
            }
        }
        return false;
    }
}
