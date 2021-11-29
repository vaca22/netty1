package fuck;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;


public class UdpServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)

                    .channel(NioDatagramChannel.class)

                    .option(ChannelOption.SO_BROADCAST, true)

                    .option(ChannelOption.SO_RCVBUF, 2048 * 1024)

                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {

                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NioEventLoopGroup(), new UdpServerHandler());
                        }
                    });

            ChannelFuture f = bootstrap.bind(8888).sync();
            System.out.println("服务器正在监听......");
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}