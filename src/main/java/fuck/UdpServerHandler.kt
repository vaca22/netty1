package fuck


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


    val ipArray=ArrayList<FuckIp>()

    fun addIp(s: FuckIp){
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
        val receiveJson=JSONObject(receive)
        val innerIp=receiveJson.getString("ip")
        val innerPort=receiveJson.getInt("port")

        val sourceInfo=packet.sender()
        val port=sourceInfo.port
        var ipAddr=sourceInfo.address
        val ipInfo= FuckIp(ip2String(ipAddr),port,innerIp,innerPort)
        println("来源：${ip2String(ipAddr)}:${port}")
        addIp(ipInfo)


        if(ipArray.size==2){
            var mmp: FuckIp?=null
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
                vb.put("innerIp",mmp.innerIp)
                vb.put("innerPort",mmp.innerPort)
                val byteBuf = Unpooled.copiedBuffer(vb.toString().toByteArray())
                ctx.writeAndFlush(DatagramPacket(byteBuf, sourceInfo))
            }
        }
    }
}