package com.nio;

import java.io.IOException;

/**
 * @author liyun
 * @date 2019/8/21 23:39
 */
public class BClient {

    public static void main(String[] args) throws IOException {
        NioClient nioClient = new NioClient();
        nioClient.start("bClient");
    }
}
