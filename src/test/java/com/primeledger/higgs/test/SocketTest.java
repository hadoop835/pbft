package com.primeledger.higgs.test;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class SocketTest {
    @Test
    public void socketTest(){
        try {
            Socket socket = new Socket("127.0.0.1",1326);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
