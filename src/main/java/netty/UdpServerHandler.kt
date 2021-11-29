package netty

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets

/**
 * 服务端业务处理
 *
 * @author LionLi
 */
class UdpServerHandler : SimpleChannelInboundHandler<DatagramPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext, packet: DatagramPacket) {
        println("服务端接收到消息：" + packet.content().toString(StandardCharsets.UTF_8))
        // 向客户端发送消息
        val byteBuf = Unpooled.copiedBuffer("已经接收到消息!".toByteArray(StandardCharsets.UTF_8))
        val fuck=packet.sender()
        val ff=fuck.port
        println("服务端接收到消息ff："+ff);
        ctx.writeAndFlush(DatagramPacket(byteBuf, packet.sender()))
    }
}