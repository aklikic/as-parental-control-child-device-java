package com.lightbend.gsa.parentalcontrol.childdevice;

import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.google.protobuf.Empty;
import org.junit.Test;
import org.mockito.*;

public class ChildDeviceServiceTest {
    private String entityId = "entityId1";
    private ChildDeviceServiceImpl entity;
    private CommandContext context = Mockito.mock(CommandContext.class);
    
    @Test
    public void createChildDeviceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.createChildDevice(PcChildDeviceApi.CreateChildDeviceCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void deleteChildDeviceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.deleteChildDevice(PcChildDeviceApi.DeleteChildDeviceCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void getChildDeviceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.getChildDevice(PcChildDeviceApi.GetChildDeviceCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void addParentDeviceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.addParentDevice(PcChildDeviceApi.AddParentCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void removeParentTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.removeParent(PcChildDeviceApi.RemoveParentCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void addControlGeofenceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.addControlGeofence(PcChildDeviceApi.AddControlGeofenceCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void updateControlGeofenceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.updateControlGeofence(PcChildDeviceApi.UpdateControlGeofenceCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void removeControlGeofenceTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.removeControlGeofence(PcChildDeviceApi.RemoveControlGeofenceCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
    
    @Test
    public void addGpsLocationTest() {
        entity = new ChildDeviceServiceImpl(entityId);
        
        // TODO: you may want to set fields in addition to the entity id
        //    entity.addGpsLocation(PcChildDeviceApi.AddGpsLocationCommand.newBuilder().setEntityId(entityId).build(), context);
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
}