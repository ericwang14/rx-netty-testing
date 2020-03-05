package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;

import java.util.Iterator;

@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private final Emitter<String> emitter;

    public RequestHandler(Emitter<String> emitter) {
        this.emitter = emitter;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
//        System.out.println(msg.headers().toString());


        if (msg.status() == HttpResponseStatus.OK) {
            emitter.onNext(msg.content().toString(CharsetUtil.UTF_8));
            emitter.onCompleted();
        } else {
            emitter.onError(new Throwable(msg.content().toString(CharsetUtil.UTF_8)));
        }
        ctx.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
