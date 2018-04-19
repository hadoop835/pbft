package com.primeledger.higgs.pbft;


import com.primeledger.higgs.pbft.client.Client;
import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.network.impl.DefaultCommitConsensus;
import com.primeledger.higgs.pbft.server.main.Replica;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@Controller
public class PBFTAlgorithm {


    @Autowired
    Config config;

//    private int id;

    private List<Replica> replicas;

    private Client client = null;

    private static int id = 0;

    public PBFTAlgorithm() {
//        replicas = new ArrayList<>();
//        id = 0;
    }

    @RequestMapping("/incReplica")
    @ResponseBody
    public String incReplica() {
        Config c = new Config();
        BeanUtils.copyProperties(config, c);
        c.setId(id);
        c.setLogPath(c.getLogPath() + "/log" + id);
        Replica replica = new Replica(c);
        replica.setCommitConsensus(new DefaultCommitConsensus());
        replicas.add(replica);
        id++;

        return "success generate replica:" + c.getId();
    }

    @RequestMapping("/testCons")
    @ResponseBody
    public String testCons(String command) {
        if (client == null) {
            client = new Client(config);

        }
        try {
            client.postTask(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "post task success!";
    }

    @PostConstruct
    public void init() {
        System.out.println("------------start replica " + id + "----------");
        config.setId(id);
        config.setLogPath(config.getLogPath() + "/log" + id);
        Replica replica = new Replica(config);
        replica.setCommitConsensus(new DefaultCommitConsensus());

        if (client == null) {
            client = new Client(config);
        }
    }

    public static void main(String args[]) {
        id = Integer.parseInt(args[0]);
        SpringApplication.run(PBFTAlgorithm.class, args);
    }
}
