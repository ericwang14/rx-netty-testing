import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", CharsetUtil.UTF_8);
        ByteBuf slicedBuf = buf.slice(0, 15);
        System.out.println(slicedBuf.toString(CharsetUtil.UTF_8));

        buf.setByte(0, (byte)'J');

        assert buf.getByte(0) == slicedBuf.getByte(0);

    }
}
