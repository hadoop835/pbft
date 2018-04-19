package com.primeledger.higgs.pbft.server.worker;

import com.primeledger.higgs.pbft.common.message.BaseMessage;
import com.primeledger.higgs.pbft.common.message.ConsensusMessage;
import com.primeledger.higgs.pbft.common.message.MessageType;
import com.primeledger.higgs.pbft.common.message.RequestMessage;
import com.primeledger.higgs.pbft.common.utils.MessageUtils;
import com.primeledger.higgs.pbft.server.main.ServerViewController;
import com.primeledger.higgs.pbft.server.consensus.ConsensusManager;
import com.primeledger.higgs.pbft.server.consensus.EPoch;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClientMessagerResolver extends Thread {

    private BlockingQueue<BaseMessage> outQueue = null;

    private BlockingQueue<BaseMessage> requestQueue = null;

    private ReentrantLock messageLock = new ReentrantLock();

    private Condition canPrepare = messageLock.newCondition();

    private boolean doWork = true;

    private ServerViewController controller = null;

    private ConsensusManager consensusManager = null;

    public ClientMessagerResolver(ConsensusManager consensusManager, BlockingQueue<BaseMessage> outQueue, BlockingQueue<BaseMessage> requestQueue, ServerViewController controller) {
        this.outQueue = outQueue;
        this.requestQueue = requestQueue;
        this.controller = controller;
        this.consensusManager = consensusManager;

    }

    @Override
    public void run() {
        while (doWork) {
            try {
                BaseMessage message = requestQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (message == null) {
                    continue;
                }

//                controller.isHaveMsgProcess();
                messageLock.lock();

                if (message.getType() == MessageType.REQUEST) {
                    RequestMessage request = (RequestMessage) message;

                    boolean verifySig = MessageUtils.verifySignature(controller.getPublicKey(message.getSender()), request.getSerializeMessage(), request.getSignature());
                    if (!verifySig) {
                        System.out.println("invalid signature from client:" + request.getSender());
                        return;
                    }
                    //am I the leader?
                    EPoch ePoch = consensusManager.getEPoch(request.getSender(), request.getTimeaStamp());
                    if(ePoch == null){
                        continue;
                    }
                    byte requestSeriaize[] = request.getSerializeMessage();
                    byte[] digest = MessageUtils.computeDigest(requestSeriaize);
                    ePoch.setClientId(request.getSender());
                    ePoch.setLastProcessTime(System.currentTimeMillis());
                    ePoch.setMyDigest(digest);
                    ePoch.addCommitDigest(digest, controller.getMyId());
                    ePoch.addPrepareDigest(digest, controller.getMyId());
                    ePoch.setRequest(request.getOperation());
                    ePoch.setView(controller.getCurrentView());


                    if (controller.amITheLeader()) {
//                        controller.setHaveMsgProcess();
                        ConsensusMessage consensusMessage = new ConsensusMessage();
                        consensusMessage.setSender(controller.getMyId());
                        consensusMessage.setType(MessageType.PRE_PREPARE);
                        consensusMessage.setView(controller.getCurrentView());
                        consensusMessage.setTimeStamp(request.getTimeaStamp());
                        consensusMessage.setClientId(request.getSender());

                        consensusMessage.setCp(controller.getStableCp());
                        consensusMessage.setRequest(requestSeriaize);

                        consensusMessage.setDigest(digest);

                        byte signature[] = MessageUtils.signMessage(controller.getPrivateKey(), consensusMessage.getSerializeMessage());
                        consensusMessage.setSignature(signature);

                        outQueue.offer(consensusMessage);
                    }
                }
                messageLock.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

}
