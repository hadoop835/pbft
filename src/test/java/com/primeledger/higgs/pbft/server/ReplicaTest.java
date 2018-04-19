package com.primeledger.higgs.pbft.server;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import com.primeledger.higgs.pbft.server.main.Replica;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Config.class, NodeInfo.class})
public class ReplicaTest {

    @Autowired
    Config config;

    @Test
    public void testConStructor(){
        Replica replica = new Replica(config);
    }


}