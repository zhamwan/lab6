package com.server;

import com.common.Request;
import com.common.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChannelConnection {

    private int port;
    private ServerRequestHandler requestHandler;
    private ServerInputHandler serverInputHandler;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public ChannelConnection(int port, ServerRequestHandler requestHandler, ServerInputHandler serverInputHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        this.serverInputHandler = serverInputHandler;
        serverInputHandler.start();
    }

    
    private Request deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Request request = (Request) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return request;
    }

    private byte[] serialize(Response response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }

    public void sendResponse(Response r) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(65536);
        SocketChannel channel = null;
        while (channel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isWritable()) {
                    channel = (SocketChannel) key.channel();
                    byte[] responseBytes = serialize(r);
                    buffer.put(responseBytes);
                    buffer.flip();
                    channel.write(buffer);
                    channel.register(selector, SelectionKey.OP_READ);
                    buffer.clear();
            }
        }
    }}

    public Request getRequest() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(65336);
        SocketChannel channel = null;
        while (channel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            try{
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                if (key.isReadable()) {
                    channel = (SocketChannel) key.channel();
                    channel.read(buffer);
                    buffer.flip();
                    if (!buffer.hasRemaining()) continue;
                    Request r = deserialize(buffer.array());
                    channel.register(selector, SelectionKey.OP_WRITE);
                    buffer.clear();
                    return r;
                }
            }}catch (SocketException e){
                channel.close();
                System.out.println("разрыв с клиентом");
            }
        }
        channel.close();
        return null;
    }

    public void resolveConnection(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel channel = null;
        while (channel == null) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();
                if (selectionKey.isAcceptable()) {
                    channel = serverSocketChannel.accept();
                    selectionKeys.remove(selectionKey);
                    if (channel != null) {
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    } else break;
                }
            }
            return;
        }
    }

    public void start()  {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                startProcessing(serverSocketChannel);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


public void startProcessing(ServerSocketChannel serverSocketChannel) throws IOException, ClassNotFoundException {
    resolveConnection(serverSocketChannel);
    Request request = getRequest();
    if (request == null) return;
    Response response = requestHandler.processClientRequest(request);
    sendResponse(response);
}}