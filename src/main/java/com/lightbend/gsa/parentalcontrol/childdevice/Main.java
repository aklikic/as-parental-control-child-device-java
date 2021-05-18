package com.lightbend.gsa.parentalcontrol.childdevice;

import com.akkaserverless.javasdk.AkkaServerless;
import com.lightbend.gsa.parentalcontrol.childdevice.actions.PcChilddevicePublish;
import com.lightbend.gsa.parentalcontrol.childdevice.persistence.PcChilddeviceDomain;

public final class Main {
    
    public static void main(String[] args) throws Exception {
        new AkkaServerless()
            .registerEventSourcedEntity(
                ChildDeviceServiceImpl.class,
                PcChildDeviceApi.getDescriptor().findServiceByName("ChildDeviceService"),
                PcChilddeviceDomain.getDescriptor()
            )
            .registerAction(
                PublishParentInfoAction.class,
                PcChilddevicePublish.getDescriptor().findServiceByName("PublishParentInfoService"),
                PcChilddeviceDomain.getDescriptor()
            )
            .start().toCompletableFuture().get();
    }
    
}