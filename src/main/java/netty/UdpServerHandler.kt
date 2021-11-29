package netty

import IpInfo
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import org.json.JSONObject
import java.net.InetAddress
import java.nio.charset.StandardCharsets
/**
 * 服务端业务处理
 *
 * @author LionLi
 */
class UdpServerHandler : SimpleChannelInboundHandler<DatagramPacket>() {


    val ipArray=ArrayList<IpInfo>()

    fun addIp(s:IpInfo){
        if(!ipArray.contains(s)){
            ipArray.add(s)
        }
    }



    fun string2Ip(s:String):InetAddress{
        return InetAddress.getByName(s)
    }


    fun ip2String(s:InetAddress):String{
        var ip=s.toString()
        ip=ip.substring(ip.lastIndexOf("/")+1)
        return ip
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: DatagramPacket) {
        val receive=packet.content().toString(StandardCharsets.UTF_8)

        println("服务端接收到消息：" + receive)


        val sourceInfo=packet.sender()
        val port=sourceInfo.port
        var ipAddr=sourceInfo.address
        val ipInfo=IpInfo(ip2String(ipAddr),port)
        println("来源：${ip2String(ipAddr)}:${port}")
        addIp(ipInfo)


        if(ipArray.size==2){
            var mmp:IpInfo?=null
            for(k in ipArray){
                if(k!=ipInfo){
                    mmp=k
                    break;
                }
            }
            if(mmp!=null){
                val vb=JSONObject()
                vb.put("ip", mmp.ip)
                vb.put("port",mmp.port)
                val byteBuf = Unpooled.copiedBuffer(vb.toString().toByteArray())
                ctx.writeAndFlush(DatagramPacket(byteBuf, sourceInfo))
            }
        }
    }
}