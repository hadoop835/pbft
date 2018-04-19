package com.primeledger.higgs.pbft.server.main;

import com.primeledger.higgs.pbft.common.Config;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServerViewController {

    private Config config;

    private int leader;

    private int id;

    private int currentView = 0;

    /**
     * the highest check point in queue
     */
    private AtomicInteger highCp;


    /**
     * the stable check point in the file
     */
    private AtomicInteger stableCp;

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private boolean haveMsgProcess = false;


    /**
     * the lowest check point use to delete the log
     */
    private int lowStableCp;


    public ServerViewController(Config config) {
        this.config = config;
        leader = config.getInitLeader();
        id = config.getId();
        stableCp = new AtomicInteger();
        highCp = new AtomicInteger();
    }

    public void initStableCp(int cp) {
        stableCp.set(cp);
        highCp.set(cp + 1);
    }

    public int incHighCp() {
        return highCp.incrementAndGet();
    }

    public int[] getCurrentViewAcceptor() {
        return config.getInitViewAcceptors();
    }

    public int getCurrentLeader() {
        return leader;
    }

    public boolean amITheLeader() {
        return leader == id;
    }

    public int getMyId() {
        return id;
    }

    public int getCurrentView() {
        return currentView;
    }

    public PrivateKey getPrivateKey() {
        return config.getPrivateKey();
    }

    public int getCurrentViewN() {
        return config.getNodeCount();
    }

    public int getPrepareQuarum() {
        return config.getFault() + 1;
    }

    public int getCommitQuarum() {
        return config.getFault() * 2 + 1;
    }

    public PublicKey getPublicKey(int i) {
        return config.getPublicKey(i);
    }

    public int getHighCp() {
        return highCp.get();
    }

    public void setHighCp(int cp) {
        highCp.set(cp);
    }

    public int getStableCp() {
        return stableCp.get();
    }

    public int incStableCp() {
        return stableCp.incrementAndGet();
    }


    public void isHaveMsgProcess() {
        if (haveMsgProcess) {
            lock.lock();
            condition.awaitUninterruptibly();
            lock.unlock();
        }
    }

    public int getLowStableCp() {
        return lowStableCp;
    }

    public void setLowStableCp(int lowStableCp) {
        this.lowStableCp = lowStableCp;
    }


    public void setHaveMsgProcess(){
        haveMsgProcess = true;
    }


    public void processDown() {
        haveMsgProcess = false;
        condition.signal();
    }
}
