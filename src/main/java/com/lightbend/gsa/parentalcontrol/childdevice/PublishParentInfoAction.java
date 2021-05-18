package com.lightbend.gsa.parentalcontrol.childdevice;

import com.akkaserverless.javasdk.action.Action;
import com.akkaserverless.javasdk.action.ActionContext;
import com.akkaserverless.javasdk.action.ActionReply;
import com.akkaserverless.javasdk.action.Handler;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.lightbend.gsa.parentalcontrol.childdevice.persistence.PcChilddeviceDomain;

@Action
public class PublishParentInfoAction {
    @Handler
    public PcChilddeviceDomain.ParentAdded publishParentAdded(PcChilddeviceDomain.ParentAdded event) {
        return event;
    }

    @Handler
    public PcChilddeviceDomain.ParentRemoved publishParentRemoved(PcChilddeviceDomain.ParentRemoved event) {
        return event;
    }

    @Handler
    public Empty catchOthers(Any Event) {
        return Empty.getDefaultInstance();
    }
}
