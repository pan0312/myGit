package com.wp.nio_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public void start() throws IOException {
        Selector selector =Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8000));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功！ ");
        for (;;){
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()){
                    acceptHandler(serverSocketChannel, selector);
                }
                if (selectionKey.isReadable()){
                    readHandler(selectionKey, selector);
                }
            }
        }
    }

    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        socketChannel.write(Charset.forName("UTF-8")
                .encode("你与聊天室里其他人都不是朋友关系，请注意隐私安全"));
    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        String request = "";
        while (socketChannel.read(byteBuffer) > 0){
            byteBuffer.flip();
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }
        socketChannel.register(selector, SelectionKey.OP_READ);
        if (request.length() > 0){
            broadCast(selector,socketChannel,request);
        }
    }

    private void broadCast(Selector selector, SocketChannel sourceChannel, String request){
        Set<SelectionKey> selectionKeys = selector.keys();
        selectionKeys.forEach(selectionKey ->{
            Channel targetChannel = selectionKey.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != sourceChannel){
                try {
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
