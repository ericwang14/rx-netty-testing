import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("server received: " + in.toString(CharsetUtil.UTF_8));
//        ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
//            System.out.println("remote address: " + ctx.channel().remoteAddress());
//            System.out.println("remote connection still alive?  " + ctx.channel().isActive());
//            System.out.println("looping event " + Thread.currentThread().getName());
//            ctx.channel().eventLoop().parent().forEach(event -> System.out.println(event.toString()));
//        }, 10, 10, TimeUnit.SECONDS);

        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        System.out.println("write successful!");
                    } else {
                        future.cause().printStackTrace();
                    }
                })
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
