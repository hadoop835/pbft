package com.primeledger.higgs.pbft.server.log;

import com.primeledger.higgs.pbft.common.message.StateLog;
import com.primeledger.higgs.pbft.common.utils.CommonUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;

public class LogToLevelDB implements ILogState {

    DB db = null;

    public LogToLevelDB(String path) {
        DBFactory factory = Iq80DBFactory.factory;
        Options options = new Options().createIfMissing(true);
        File dir = new File(path);

        try {
            db = factory.open(dir, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getStableCp() {
        byte[] b = db.get("stableCp".getBytes());
        if (b == null) {
            return 0;
        } else {
            return CommonUtil.bytesToInt(b);
        }

    }

    @Override
    public void putStableCp(int cp) {
        db.put("stableCp".getBytes(), CommonUtil.intToBytes(cp));
    }


    @Override
    public void putLog(StateLog stateLog) {
        db.put(CommonUtil.intToBytes(stateLog.getCp()), stateLog.getOperation());
    }

    @Override
    public StateLog[] getStateLog(int start, int end) {
        StateLog[] stateLogs = new StateLog[end - start + 1];
        for (int i = 0; i < stateLogs.length; i++) {
            stateLogs[i] = new StateLog();
            stateLogs[i].setCp(start + i);
            stateLogs[i].setOperation(db.get(CommonUtil.intToBytes(start + i)));
        }
        return stateLogs;
    }

    @Override
    public int getLowStableCp() {
        byte[] b = db.get("lowStableCp".getBytes());
        if (b == null) {
            return 0;
        } else {
            return CommonUtil.bytesToInt(b);
        }
    }

    @Override
    public void setLowStableCp(int cp) {
        db.put("lowStableCp".getBytes(), CommonUtil.intToBytes(cp));
    }

    @Override
    public void deleteLog(int start, int end) {
        for (int i = start; i <= end; i++){
            db.delete(CommonUtil.intToBytes(i));
        }
    }


    public void close() {
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
