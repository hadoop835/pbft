package com.primeledger.higgs.pbft.server.consensus;

import com.primeledger.higgs.pbft.server.main.ServerViewController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConsensusManager {

    /**
     * client time latest stamp cache
     */
    private Map<Integer, Long> clientRequestTimeStamp = null;

    /**
     * save according to client id and time stamp
     */
    private Map<Integer, Map<Long, EPoch>> consensusMap = null;

    /**
     * client request queue
     */
    private BlockingQueue clientReuqestQueue = null;

    /**
     * the whole server view controller
     */
    private ServerViewController controller = null;

    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();


    public ConsensusManager(ServerViewController controller) {
        this.controller = controller;
        this.consensusMap = new HashMap<>();
        clientRequestTimeStamp = new HashMap<>();
    }

    public EPoch getEPoch(int client, long timeStamp) {
        rw.writeLock().lock();
        Map<Long, EPoch> map = consensusMap.get(client);

        if (map == null) {
            map = new HashMap<>();
            consensusMap.put(client, map);
        }
        EPoch epoch = map.get(timeStamp);

        if (epoch == null) {
            Long lastTime = clientRequestTimeStamp.get(client);
            if (lastTime != null) {
                if (lastTime > timeStamp) {
                    return null;
                } else {
                    Long[] t = (Long[]) map.keySet().toArray();
                    for (Long l : t) {
                        EPoch e = map.get(l);
                        if (l + 2000L < lastTime && e != null && e.isCommit()) {

                            map.remove(l);
                        }
                    }
                    map.remove(lastTime);
                    clientRequestTimeStamp.put(client, timeStamp);
                }
            }
            epoch = new EPoch(controller, client, timeStamp);
            map.put(timeStamp, epoch);
        }
        rw.writeLock().unlock();
        return epoch;
    }

    public void run() {

    }

}
