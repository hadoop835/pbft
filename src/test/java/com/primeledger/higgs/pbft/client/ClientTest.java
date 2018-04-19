package com.primeledger.higgs.pbft.client;

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
public class ClientTest {

    @Autowired
    Config config;

    @Test
    public void testConstruct(){
//        Client client = new Client(config.getServerId(),config.getNodeInfos());
    }


}