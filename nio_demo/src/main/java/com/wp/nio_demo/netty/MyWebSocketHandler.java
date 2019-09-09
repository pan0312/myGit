package com.wp.nio_demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

public class MyWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    private static final String WEB_SOCKET_URL = "ws://localhost:8888/websocket";

    /**
     * 客户端与服务端建立连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        NettyConfig.group.add(ctx.channel());
        System.out.println("客户端与服务端连接开启！");
    }

    /**
     * 出现异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 客户端与服务端断开连接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //super.channelInactive(ctx);
        NettyConfig.group.remove(ctx.channel());
        System.out.println("客户端与服务端连接关闭！");
    }

    /**
     * 服务端接收客户端发送过来的数据结束之后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //super.channelReadComplete(ctx);
        ctx.flush();
    }

    /**
     * 服务端处理客户端请求的核心业务方法
     * @param channelHandlerContext
     * @param o
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //处理客户端向服务端发起http握手请求
        if (o instanceof FullHttpRequest){
            handHttpRequest(channelHandlerContext,(FullHttpRequest) o);
        }else if (o instanceof WebSocketFrame){
            //处理websocket连接业务
            handWebSocketFrame(channelHandlerContext,(WebSocketFrame) o);
        }
    }

    /**
     * 处理客户端与服务端的websocket业务
     * @param ctx
     * @param frame
     */
    private void handWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        //判断是否是关闭websocket命令
        if (frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(),((CloseWebSocketFrame) frame).retain());
        }
        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //判断是否是二进制消息
        if (!(frame instanceof TextWebSocketFrame)){
            System.out.println("暂不支持二进制消息！");
            throw new RuntimeException("【"+this.getClass().getName()+"】不支持消息");
        }
        //返回应答消息
        String request = ((TextWebSocketFrame)frame).text();  //获取客户端向服务端发送的消息
        System.out.println("服务端收到客户端的消息=======>>>>>"+request);
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()+ctx.channel().id()+"===>>>"+request);
        //服务端向每个已连接的客户端发送消息
        NettyConfig.group.writeAndFlush(tws);
    }

    /**
     * 处理客户端向服务端发起http握手请求的业务
     * @param ctx
     * @param request
     */
    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request){
        if (!request.getDecoderResult().isSuccess()
                || !"websocket".equals(request.headers().get("Upgrade"))){
            sendHttpResponse(ctx,request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsf = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL,null,false);
        handshaker = wsf.newHandshaker(request);
        if (handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(),request);
        }
    }

    /**
     * 服务端向客户端响应消息
     * @param ctx
     * @param request
     * @param response
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request,
                                  DefaultFullHttpResponse response){
        if (response.getStatus().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }
        //服务端向客户端发送数据
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (response.getStatus().code() != 200){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
