package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Nio 客户端
 * @author li
 * @date 2019/8/21
 */
public class NioClient {

    /**
     * 启动
     */
    public void start(String nickName) throws IOException {
        //连接服务器端
        System.out.println("正在连接服务器...");
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));

        //接收服务器端响应
        //新开一个线程，专门负责接收服务器端数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        //向服务器端发送数据
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()){
            String request = sc.nextLine();
            if (request != null && !"".equals(request)){
                socketChannel.write(Charset.forName("UTF-8").encode(nickName + ": " +request));
            }
        }
    }
}
