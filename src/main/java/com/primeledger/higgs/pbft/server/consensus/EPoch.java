package com.primeledger.higgs.pbft.server.consensus;

import com.primeledger.higgs.pbft.server.main.ServerViewController;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EPoch {

    /**
     * latest process time
     */
    private long lastProcessTime;

    private long sequence;

    /**
     * client time stamp
     */
    private long timestamp;

    /**
     * the id of client
     */
    private int clientId;

    /**
     * current view value
     */
    private int view;

    /**
     * the received digest in the prepare stage
     */
    private byte[][] prepareDigest;

    /**
     * the received digests in the commit stage
     */
    private byte[][] commitDigest;

    /**
     * digest computed by myself according to the request receive from client
     */
    private byte[] myDigest;

    /**
     * the request receive from client
     */
    private byte[] request = null;

    /**
     * if current consensus have come to commit stage
     */
    private boolean prepare;

    /**
     * if current consensus have come to commit stage
     */
    private boolean commit;

    /**
     * current consensus check point height
     */
    private int cp[];

    private int consensusCp;

    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    private ServerViewController controller;

    public EPoch(ServerViewController controller, int clientId, long timestamp) {
        this.controller = controller;
        this.clientId = clientId;
        this.timestamp = timestamp;

        int n = controller.getCurrentViewN();

        prepare = false;
        commit = false;

        prepareDigest = new byte[n][];
        commitDigest = new byte[n][];

        cp = new int[n];
        Arrays.fill(cp, -1);
    }

    public int countPrepare() {
        //TODO if need a read lock
        rw.readLock().lock();
        int cnt =  count(prepareDigest, myDigest);
        rw.readLock().unlock();
        return cnt;
    }

    public int countCommit() {
        //TODO if need a read lock
        rw.readLock().lock();
        int cnt = count(commitDigest, myDigest);
        rw.readLock().unlock();
        return cnt;
    }

    private int count(byte[][] standard, byte value[]) {
        int cnt = 0;
        if (value != null) {
            for (byte[] b : standard) {
                if (b != null && b.length > 0 && Arrays.equals(b, value)) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public boolean isPrepare() {
        return prepare;
    }

    public void setPrepare(boolean prepare) {
        this.prepare = prepare;
    }

    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }

    public long getLastProcessTime() {
        return lastProcessTime;
    }

    public void setLastProcessTime(long lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public byte[][] getPrepareDigest() {
        return prepareDigest;
    }

    public void setPrepareDigest(byte[][] prepareDigest) {
        this.prepareDigest = prepareDigest;
    }

    public byte[] getMyDigest() {
        return myDigest;
    }

    public void setMyDigest(byte[] myDigest) {
        this.myDigest = myDigest;
    }

    public byte[] getRequest() {
        return request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public void addPrepareDigest(byte[] digest, int sender) {
        rw.writeLock().lock();
        prepareDigest[sender] = Arrays.copyOf(digest,digest.length);
        rw.writeLock().unlock();
    }

    public void addCommitDigest(byte[] digest, int sender) {
        rw.writeLock().lock();
        commitDigest[sender] = Arrays.copyOf(digest,digest.length);
        rw.writeLock().unlock();
    }

    /**
     * @return the max same value in the cp
     */
    public int getMaxSameCp() {
        rw.readLock().lock();
        int[] tmp = Arrays.copyOf(cp, cp.length);
        Arrays.sort(tmp);
        int cnt = 0;
        int maxCnt = 0;
        int cur = -1;
        for (int i : tmp) {
            if (i == -1) continue;
            if (cur == i) {
                cnt++;
                consensusCp = i;
            } else {
                cur = i;
                cnt = 1;
            }
            maxCnt = (maxCnt > cnt ? maxCnt : cnt);
        }
        rw.readLock().unlock();
        return maxCnt;
    }

    public void addCp(int cp, int n) {
        rw.writeLock().lock();
        this.cp[n] = cp;
        rw.writeLock().unlock();

    }

    public int getConsensusCp(){
        return consensusCp;
    }
}
