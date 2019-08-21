package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 服务器端
 * @author li
 * @date 2019/8/21
 */
public class NioServer {


    public static void main(String[] args) {
        NioServer nioServer = new NioServer();

    }

    /**
     * 启动
     */
    public void start() throws IOException {
        //1.创建一个Selector
        Selector selector = Selector.open();

        //2.通过ServerSocketChannel创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //3.为channel通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8000));

        //4.设置channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //5.将channel注册到selector上，监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("------服务器启动成功-----");

        //6.循环等待新接入的连接
        while (true){
            //TODO 获取可用channel数量
            int readyChannels = selector.select();

            if(readyChannels == 0){
                continue;
            }
            //获取可用channel的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                // selectionKey 实例
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                //移除Set中当前selectionKey
                iterator.remove();

                //7.根据就绪状态，调用对应方法处理对应逻辑
                //TODO 如果是接入事件

                //TODO 如果是可读事件
            }
        }
    }
}
