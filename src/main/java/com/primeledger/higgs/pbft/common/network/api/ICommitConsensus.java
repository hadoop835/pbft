package com.primeledger.higgs.pbft.common.network.api;

public interface ICommitConsensus<T> {

    boolean commit(T t);
}
