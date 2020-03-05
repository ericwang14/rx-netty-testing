package ws_chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

public class SecurityChatServer extends ChatServer {
    private final SslContext sslContext;

    public SecurityChatServer(SslContext context) {
        this.sslContext = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup) {
        return new SecurityChatServerInitializer(channelGroup, sslContext);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Please give port as argument");
            System.exit(1); }
        int port = Integer.parseInt(args[0]);
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());

        final SecurityChatServer chatServer = new SecurityChatServer(sslContext);
        ChannelFuture future = chatServer.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                chatServer.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
