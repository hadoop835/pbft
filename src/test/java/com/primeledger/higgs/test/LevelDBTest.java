package com.primeledger.higgs.test;

import com.primeledger.higgs.pbft.common.utils.CommonUtil;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class LevelDBTest {

    @Test
    public void testSave(){
        String path = System.getProperty("user.dir") + "/log/log0";
        System.out.println(path);
        DBFactory factory = Iq80DBFactory.factory;
        Options options = new Options().createIfMissing(true);
        File dir = new File(path);
        try {
            DB db = factory.open(dir,options);

            db.put("123".getBytes(),CommonUtil.intToBytes(1));
//            System.out.println(db.get(CommonUtil.intToBytes(123)));
            System.out.println(CommonUtil.bytesToInt(db.get("123".getBytes())));

            byte[] test = db.get(new byte[]{3,2,3});
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
