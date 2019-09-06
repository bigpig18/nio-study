package com.nio.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author li
 * @date 2019/9/6
 */
public class FileCopy {

    public static void main(String[] args) {
        File source = new File("F:\\BaiduNetdiskDownload\\java.pdf");
        File target = new File("F:\\BaiduNetdiskDownload\\copy\\target.pdf");
        long startTime = System.currentTimeMillis();
        noBufferStreamCopy(source,target);
        //bufferStreamCopy(source,target);
        long endTime = System.currentTimeMillis();
        System.out.println("time : " + (endTime-startTime)+"ms");
    }

    private static void close(Closeable... closeable){
        if (closeable != null){
            try {
                for (Closeable aCloseable : closeable) {
                    aCloseable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 最原始的方法将文件拷贝
     * @param source 源文件
     * @param target 目标文件
     */
    private static void noBufferStreamCopy(File source,File target){
        InputStream fin = null;
        OutputStream fout = null;

        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);
            int result;
            while((result = fin.read()) != -1){
                fout.write(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(fin,fout);
        }
    }

    /**
     * buffer(缓冲区)Stream拷贝目标文件
     * @param source 源文件
     * @param target 目标文件
     */
    private static void bufferStreamCopy(File source,File target){
        InputStream fin = null;
        OutputStream fout = null;

        try {
            fin = new BufferedInputStream(new FileInputStream(source));
            fout = new BufferedOutputStream(new FileOutputStream(target));

            byte[] buffer = new byte[1024];
            int result;
            while((result = fin.read(buffer)) != -1){
                fout.write(buffer,0,result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(fin,fout);
        }
    }

    /**
     * nioBuffer拷贝文件
     * @param source 源文件
     * @param target 目标文件
     */
    private static void nioBufferCopy(File source,File target){
        FileChannel fin = null;
        FileChannel fout = null;
        try {
            fin = new FileInputStream(source).getChannel();
            fout = new FileOutputStream(target).getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(fin.read(buffer) != -1){
                buffer.flip();
                while(buffer.hasRemaining()){
                    fout.write(buffer);
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(fin,fout);
        }
    }

    private static void nioTransferCopy(File source,File target){
        FileChannel fin = null;
        FileChannel fout = null;

        try {
            fin = new FileInputStream(source).getChannel();
            fout = new FileOutputStream(target).getChannel();

            long transferred = 0L;
            long size = fin.size();
            while(transferred != size){
                transferred += fin.transferTo(0,size,fout);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(fin,fout);
        }
    }
}
