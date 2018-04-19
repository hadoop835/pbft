package com.primeledger.higgs.pbft.common.network.impl;

import com.primeledger.higgs.pbft.common.network.api.ICommitConsensus;
import com.primeledger.higgs.pbft.common.utils.CommonUtil;
import com.primeledger.higgs.pbft.common.utils.MessageUtils;

import java.io.IOException;

public class DefaultCommitConsensus implements ICommitConsensus<byte[]>{

    @Override
    public boolean commit(byte[] o) {
        try {
            Object object = MessageUtils.byteToObj(o);
            System.out.println("execute :"+object);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
}
