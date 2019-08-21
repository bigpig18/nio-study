package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 服务器端
 * @author li
 * @date 2019/8/21
 */
public class NioServer {


    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
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
                if (selectionKey.isAcceptable()){
                    accepctHandler(serverSocketChannel,selector);
                }
                //TODO 如果是可读事件
                if (selectionKey.isReadable()){
                    readHandler(selectionKey,selector);
                }
            }
        }
    }

    /**
     * 接入事件处理器
     */
    private void accepctHandler(ServerSocketChannel serverSocketChannel,Selector selector) throws IOException {
        //如果是接入事件，创建socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        //将Channel设置为非阻塞工作模式
        socketChannel.configureBlocking(false);
        //将channel注册都selector上，监听可读事件
        socketChannel.register(selector,SelectionKey.OP_READ);
        //回复客户端提示信息
        socketChannel.write(Charset.forName("UTF-8").encode("已连接上服务器..."));
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
        //从selectionKey中获取已就绪channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取客户端请求信息
        String request = "";
        while (socketChannel.read(byteBuffer) > 0){
            //切换buffer为读模式
            byteBuffer.flip();
            //读取buffer中内容
            request += Charset.forName("UTF-8").decode(byteBuffer);

        }
        //将channel再次注册到selector中，监听其可读事件
        socketChannel.register(selector,SelectionKey.OP_READ);
        //将客户端发送的请求信息广播给其它客户端
        if (request.length() > 0){
            //广播给其他客户端
            broadCast(selector,socketChannel,request);
        }
    }

    /**
     * 广播给其他客户端
     */
    private void broadCast(Selector selector,SocketChannel socketChannel,String request) throws IOException {
        //获取到所有已接入的channel
        Set<SelectionKey> selectionKeys = selector.keys();

        //循环向所有channel广播信息
        for (SelectionKey selectionKey : selectionKeys){
            Channel targetChannel = selectionKey.channel();

            //剔除发消息的客户端
            if (targetChannel instanceof SocketChannel && targetChannel != socketChannel){
                //将消息发送到targetChannel客户端
                ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
            }
        }
    }
}
