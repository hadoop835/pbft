package com.primeledger.higgs.test;

import com.primeledger.higgs.pbft.client.Client;
import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Config.class, NodeInfo.class})
public class BechMarkTest {

    @Autowired
    private Config config;

    @Test
    public void testBenchMark() {
        config.setId(2);
        Client client = new Client(config);

        long start = System.currentTimeMillis();
        System.out.println("start time " + start);
        for (int i = 0; i < 100000; i++) {
            try {
                Thread.sleep(50);
                String command = "test" + i;
                client.postTask(command);
                System.out.println("send command " + command);

            } catch (IOException e) {
                e.printStackTrace();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
