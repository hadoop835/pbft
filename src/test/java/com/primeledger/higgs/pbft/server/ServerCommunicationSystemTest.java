package com.primeledger.higgs.pbft.server;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes={Config.class, NodeInfo.class})
public class ServerCommunicationSystemTest {

    @Autowired
    private Config config;

    @Test
    public void testConstruction(){
        System.out.println("----------------test construct start----------------");

        NodeInfo myNode = config.getMyNodeInfo();
//        ServerCommunicationSystem scs = new ServerCommunicationSystem(config);

    }

}