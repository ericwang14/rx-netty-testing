import handler.OutHandler;
import handler.RequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.http.client.HttpClient;
import rx.Emitter;
import rx.Observable;
import rx.observables.ConnectableObservable;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class Request {

    private static final Bootstrap bootstrap = new Bootstrap();
    private static final EventLoopGroup group = new NioEventLoopGroup(1);

    static {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .remoteAddress(new InetSocketAddress("services.shop.com", 8085))
        .handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast("log", new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpContentDecompressor());
                pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
//                            pipeline.addLast(new OutHandler(uri));
            }
        });
    }
    public static Observable<String> get(String uri) {
        return Observable.create((emitter) -> {
        try {
            // Make the connection attempt.
            bootstrap.connect().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        Channel ch = future.channel();

                        ch.pipeline().addLast(new RequestHandler(emitter));
                        // Prepare the HTTP request.
                        HttpRequest request = new DefaultFullHttpRequest(
                                HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
                        request.headers().set(HttpHeaderNames.HOST, "services.shop.com:8085");
                        request.headers().set(HttpHeaderNames.ACCEPT, "*/*");
                        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

                        // Send the HTTP request.
                        ch.writeAndFlush(request);
                    } else {
                        emitter.onError(future.cause());
                    }

                }
            });


            // Wait for the server to close the connection.
        } catch (Exception e ) {
            emitter.onError(e);
        }

        }, Emitter.BackpressureMode.BUFFER);
    }

    public static void main(String[] args) throws IOException {

//        ConnectableObservable<String> observable = Request.get("/Site/260").publish();
//        observable.subscribe(result -> System.out.println(result), Throwable::printStackTrace);
//        observable.subscribe(result -> {
//            System.out.println("process result 2");
//            System.out.println(result);
//        }, Throwable::printStackTrace);
//
//        observable.connect();



        long start = System.currentTimeMillis();
        Iterable<String> iterator = Observable.merge(
                List.of(Request.get("/Site/260"),
                        Request.get("/Site/898"),
                        Request.get("/Site/13663"),
                        Request.get("/Site/23693"),
                        Request.get("/Site/13693"),
                        Request.get("/Site/26003"),
                        Request.get("/Site/26007"),
                        Request.get("/Site/HomePage/SHOP/260"),
                        Request.get("/Site/HomePage/SHOP/13663"),
                        Request.get("/Site/HomePage/SHOP/23693"))
                )
                .toBlocking()
                .toIterable();

        for (String value : iterator) {
            System.out.println(value);
        }


        System.out.println("total duration : " + (System.currentTimeMillis() - start ) + "ms");

        long a = System.currentTimeMillis();
        Request.get("/Site/334/States?siteId=260").subscribe(s -> {
            System.out.println(" total duration : " + (System.currentTimeMillis() - a ) + "ms");
            System.out.println(s);
        }, Throwable::printStackTrace);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public synchronized void start() {
                group.shutdownGracefully().syncUninterruptibly();
            }
        });

    }
}
