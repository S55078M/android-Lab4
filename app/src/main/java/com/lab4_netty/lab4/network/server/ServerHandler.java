package com.lab4_netty.lab4.network.server;

import com.lab4_netty.lab4.utils.JSEvaluator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Custom handler sends back all messages received from the clients
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        String result = runCode(messagePreprocessing(message));
        ctx.writeAndFlush("run() = " + result);
    }

    private String runCode(String code) {
        return JSEvaluator.eval("run", code, new Object[] {});
    }

    private String messagePreprocessing(String msg) {
        return msg.replaceAll("[\r\n]+", " ");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}