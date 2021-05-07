package io.vlingo.xoom.auth.implnative;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.wire.channel.ResponseChannelConsumer;
import io.vlingo.xoom.wire.fdx.bidirectional.ClientRequestResponseChannel;
import io.vlingo.xoom.wire.fdx.bidirectional.netty.client.NettyClientRequestResponseChannel;
import io.vlingo.xoom.wire.node.Address;
import io.vlingo.xoom.wire.node.AddressType;
import io.vlingo.xoom.wire.node.Host;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import java.util.concurrent.atomic.AtomicInteger;

public final class NativeImpl {
    @CEntryPoint(name = "Java_io_vlingo_xoom_authnative_Native_start")
    public static int start(@CEntryPoint.IsolateThreadContext long isolateId, CCharPointer name) {
        final String nameString = CTypeConversion.toJavaString(name);
        final AtomicInteger baseServerPort = new AtomicInteger(19090);
        World world = World.start(nameString);

        int serverPort = baseServerPort.getAndIncrement();

        ResponseChannelConsumer consumer = world.actorFor(ResponseChannelConsumer.class, null);

        ClientRequestResponseChannel client = new NettyClientRequestResponseChannel(Address.from(Host.of("localhost"), serverPort, AddressType.NONE), consumer, 100, 10240);

        return 0;
    }
}
