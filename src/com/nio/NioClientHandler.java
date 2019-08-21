package com.nio;

import com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类，接收服务器端响应信息
 * @author liyun
 * @date 2019/8/21 22:31
 */
public class NioClientHandler implements Runnable{

    private Selector selector;

    public NioClientHandler(Selector selector){
        this.selector = selector;
    }

    @Override
    public void run() {
        try{
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
                    //可读事件
                    if (selectionKey.isReadable()){
                        readHandler(selectionKey,selector);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
        //从selectionKey中获取已就绪channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取服务器端响应信息
        String response = "";
        while (socketChannel.read(byteBuffer) > 0){
            //切换buffer为读模式
            byteBuffer.flip();
            //读取buffer中内容
            response += Charset.forName("UTF-8").decode(byteBuffer);

        }
        //将channel再次注册到selector中，监听其可读事件
        socketChannel.register(selector,SelectionKey.OP_READ);
        //将服务的响应信息打印到本地
        if (response.length() > 0){
            System.out.println(response);
        }
    }
}
