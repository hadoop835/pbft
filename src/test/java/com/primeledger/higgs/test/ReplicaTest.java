package com.primeledger.higgs.test;

import com.primeledger.higgs.pbft.common.Config;
import com.primeledger.higgs.pbft.common.network.connection.NodeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Config.class, NodeInfo.class})
public class ReplicaTest {
    @Autowired
    private Config config;

    @Test
    public void testReplica(){


    }

    public static void main(String args[]){
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] num = new int[n];
        boolean arr[] = new boolean[n];
        for(int i=0;i<n;i++){
            num[i] = scanner.nextInt();
        }
        arr[0] = true;

        for(int i=0;i<n;i++){
            if(arr[i] == false) {System.out.println(false);break;}
            if(num[i] + i >= n-1){System.out.println(true);break;}
            for(int j=1;j<=num[i];j++){arr[j+i] = true;}
        }
    }

}
