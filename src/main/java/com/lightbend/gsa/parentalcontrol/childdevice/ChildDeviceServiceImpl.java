package com.lightbend.gsa.parentalcontrol.childdevice;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import com.lightbend.gsa.parentalcontrol.childdevice.persistence.PcChilddeviceDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "ChildDeviceEntity")
public class ChildDeviceServiceImpl extends ChildDeviceService {

    private static Logger log = LoggerFactory.getLogger(ChildDeviceServiceImpl.class);
    @SuppressWarnings("unused")
    private final String entityId;
    private PcChildDeviceApi.ChildDevice state;
    
    public ChildDeviceServiceImpl(@EntityId String entityId) {
        log.info("[{}] initialize",entityId);
        this.entityId = entityId;
    }
    
    @Override
    public PcChilddeviceDomain.ChildDeviceState snapshot() {
        log.info("[{}] snapshot",entityId);
        return convert(state);
    }
    
    @Override
    public void handleSnapshot(PcChilddeviceDomain.ChildDeviceState snapshot) {
        log.info("[{}] handleSnapshot: {}",entityId,snapshot);
        this.state = convert(snapshot);

    }
    
    @Override
    public Empty createChildDevice(PcChildDeviceApi.CreateChildDeviceCommand command, CommandContext ctx) {
        log.info("[{}] createChildDevice: {} ",entityId,command);
        if(state!=null)
            return Empty.getDefaultInstance();
        PcChilddeviceDomain.ChildDeviceCreated event = PcChilddeviceDomain.ChildDeviceCreated.newBuilder()
                .setDeviceId(command.getDeviceId())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public Empty deleteChildDevice(PcChildDeviceApi.DeleteChildDeviceCommand command, CommandContext ctx) {
        log.info("[{}] deleteChildDevice: {} ",entityId,command);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        PcChilddeviceDomain.ChildDeviceDeleted event = PcChilddeviceDomain.ChildDeviceDeleted.newBuilder()
                .setDeviceId(command.getDeviceId())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public PcChildDeviceApi.ChildDevice getChildDevice(PcChildDeviceApi.GetChildDeviceCommand command, CommandContext ctx) {
        log.info("[{}] getChildDevice: {} [{}]",entityId,command,state);
        return state;
    }
    
    @Override
    public Empty addParentDevice(PcChildDeviceApi.AddParentCommand command, CommandContext ctx) {
        log.info("[{}] addParentDevice: {} [{}]",entityId,command,state);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        PcChilddeviceDomain.ParentAdded event = PcChilddeviceDomain.ParentAdded.newBuilder()
                .setDeviceId(command.getDeviceId())
                .setParentId(command.getParentId())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public Empty removeParent(PcChildDeviceApi.RemoveParentCommand command, CommandContext ctx) {
        log.info("[{}] removeParent: {} [{}]",entityId,command,state);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        PcChilddeviceDomain.ParentRemoved event = PcChilddeviceDomain.ParentRemoved.newBuilder()
                .setDeviceId(command.getDeviceId())
                .setParentId(command.getParentId())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public Empty addControlGeofence(PcChildDeviceApi.AddControlGeofenceCommand command, CommandContext ctx) {
        log.info("[{}] addControlGeofence: {} [{}]",entityId,command,state);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        if(state.getControlGeofenceList().stream().filter(g->g.getGeofenceId().equals(command.getGeofenceId())).findAny().isPresent())
            return Empty.getDefaultInstance();
        if(!validateControlGeofencePolygon(command.getGeofencePolygon()))
            throw ctx.fail("Polygon not correct!");
        PcChilddeviceDomain.ControlGeofenceAdded event = PcChilddeviceDomain.ControlGeofenceAdded.newBuilder()
                .setDeviceId(command.getDeviceId())
                .setGeofenceId(command.getGeofenceId())
                .setGeofencePolygon(command.getGeofencePolygon())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public Empty updateControlGeofence(PcChildDeviceApi.UpdateControlGeofenceCommand command, CommandContext ctx) {
        log.info("[{}] updateControlGeofence: {} [{}]",entityId,command,state);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        if(!state.getControlGeofenceList().stream().filter(g->g.getGeofenceId().equals(command.getGeofenceId())).findAny().isPresent())
            throw ctx.fail("Control geofence does not exist!");
        if(!validateControlGeofencePolygon(command.getGeofencePolygon()))
            throw ctx.fail("Polygon not correct!");
        PcChilddeviceDomain.ControlGeofenceUpdated event = PcChilddeviceDomain.ControlGeofenceUpdated.newBuilder()
                .setDeviceId(command.getDeviceId())
                .setGeofenceId(command.getGeofenceId())
                .setGeofencePolygon(command.getGeofencePolygon())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public Empty removeControlGeofence(PcChildDeviceApi.RemoveControlGeofenceCommand command, CommandContext ctx) {
        log.info("[{}] removeControlGeofence: {} [{}]",entityId,command,state);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        if(!state.getControlGeofenceList().stream().filter(g->g.getGeofenceId().equals(command.getGeofenceId())).findAny().isPresent())
            return Empty.getDefaultInstance();
        PcChilddeviceDomain.ControlGeofenceRemoved event = PcChilddeviceDomain.ControlGeofenceRemoved.newBuilder()
                .setDeviceId(command.getDeviceId())
                .setGeofenceId(command.getGeofenceId())
                .build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }
    
    @Override
    public Empty addGpsLocation(PcChildDeviceApi.AddGpsLocationCommand command, CommandContext ctx) {
        log.info("[{}] addGpsLocation: {} [{}]",entityId,command,state);
        if(state==null)
            throw ctx.fail("Child device does not exist!");
        long timestamp = System.currentTimeMillis();
        String geofenceId = null;
        if(state.getControlGeofenceCount()>0){
            Optional<String> locationInGeofence = isPointInPolygon(command.getGpsLongitude(),command.getGpsLatitude(),state.getControlGeofenceList());
            geofenceId = locationInGeofence.orElse(null);
            if(locationInGeofence.isPresent() && !state.getCurrentGeofenceId().isEmpty()){
                if(!locationInGeofence.get().equals(state.getCurrentGeofenceId())){
                    PcChilddeviceDomain.GpsLocationChangedControlGeofence event= PcChilddeviceDomain.GpsLocationChangedControlGeofence.newBuilder()
                            .setDeviceId(command.getDeviceId())
                            .setOldGeofenceId(state.getCurrentGeofenceId())
                            .setNewGeofenceId(geofenceId)
                            .setGpsLatitude(command.getGpsLatitude())
                            .setGpsLongitude(command.getGpsLongitude())
                            .setTimestamp(timestamp)
                            .build();
                    ctx.emit(event);
                }else{
                    //ignore - location in the sane geofence...no change needed
                }
            }else if(locationInGeofence.isPresent() && state.getCurrentGeofenceId().isEmpty()){
                PcChilddeviceDomain.GpsLocationBackToControlGeofence event = PcChilddeviceDomain.GpsLocationBackToControlGeofence.newBuilder()
                        .setDeviceId(command.getDeviceId())
                        .setGpsLatitude(command.getGpsLatitude())
                        .setGpsLongitude(command.getGpsLongitude())
                        .setGeofenceId(geofenceId)
                        .setTimestamp(timestamp)
                        .build();
                ctx.emit(event);
            }else if(!locationInGeofence.isPresent() && !state.getCurrentGeofenceId().isEmpty()){
                PcChilddeviceDomain.GpsLocationOutsideOfControlGeofence  event= PcChilddeviceDomain.GpsLocationOutsideOfControlGeofence.newBuilder()
                        .setDeviceId(command.getDeviceId())
                        .setGpsLatitude(command.getGpsLatitude())
                        .setGpsLongitude(command.getGpsLongitude())
                        .setOldGeofenceId(state.getCurrentGeofenceId())
                        .setTimestamp(timestamp)
                        .build();
                ctx.emit(event);
            }else{
                //ignore - out side of geofence with no prior geofence. this can be used to implement requirement for 3 subsequent location out side of geofence to consider it out
            }
        }
        PcChilddeviceDomain.GpsLocationAdded locAddedEvent = PcChilddeviceDomain.GpsLocationAdded.newBuilder()
                .setGpsLatitude(command.getGpsLatitude())
                .setGpsLongitude(command.getGpsLongitude())
                .setTimestamp(timestamp)
                .setGeofenceId(geofenceId)
                .build();
        ctx.emit(locAddedEvent);
        return Empty.getDefaultInstance();


    }
    
    @Override
    public void childDeviceCreated(PcChilddeviceDomain.ChildDeviceCreated event) {
        log.info("[{}] childDeviceCreated: {}",entityId,event);
        state = PcChildDeviceApi.ChildDevice.newBuilder().build();
    }
    
    @Override
    public void childDeviceDeleted(PcChilddeviceDomain.ChildDeviceDeleted event) {
        log.info("[{}] childDeviceDeleted: {}",entityId,event);
        state = null;
    }
    
    @Override
    public void parentAdded(PcChilddeviceDomain.ParentAdded event) {
        log.info("[{}] parentAdded: {}",entityId,event);
        state = state.toBuilder().addParentId(event.getParentId()).build();
    }
    
    @Override
    public void parentRemoved(PcChilddeviceDomain.ParentRemoved event) {
        log.info("[{}] parentRemoved: {}",entityId,event);
        state = state.toBuilder().addAllParentId(state.getParentIdList().stream().filter(p->!p.equals(event.getParentId())).collect(Collectors.toList())).build();
    }
    
    @Override
    public void controlGeofenceAdded(PcChilddeviceDomain.ControlGeofenceAdded event) {
        log.info("[{}] controlGeofenceAdded: {}",entityId,event);
        state = state.toBuilder().addControlGeofence(PcChildDeviceApi.ControlGeofence.newBuilder()
                .setGeofenceId(event.getGeofenceId())
                .setGeofencePolygon(event.getGeofencePolygon())
                .build())
        .build();
    }
    
    @Override
    public void controlGeofenceUpdated(PcChilddeviceDomain.ControlGeofenceUpdated event) {
        log.info("[{}] controlGeofenceUpdated: {}",entityId,event);
        state = state.toBuilder().addAllControlGeofence(
                state.getControlGeofenceList().stream().map(g->{
                    if(g.getGeofenceId().equals(event.getGeofenceId()))
                        return g.toBuilder().setGeofencePolygon(event.getGeofencePolygon()).build();
                    else
                        return g;
                })
                .collect(Collectors.toList()))
                .build();
    }
    
    @Override
    public void controlGeofenceRemoved(PcChilddeviceDomain.ControlGeofenceRemoved event) {
        log.info("[{}] controlGeofenceRemoved: {}",entityId,event);
        state = state.toBuilder().addAllControlGeofence(state.getControlGeofenceList().stream().filter(g->!g.getGeofenceId().equals(event.getGeofenceId())).collect(Collectors.toList())).build();
    }

    @Override
    public void gpsLocationAdded(PcChilddeviceDomain.GpsLocationAdded event) {
        log.info("[{}] gpsLocationAdded: {}",entityId,event);
        state = state.toBuilder()
                .setLastGpsLatitude(event.getGpsLatitude())
                .setLastGpsLongitude(event.getGpsLongitude())
                .setLastGpsTimestamp(event.getTimestamp())
                .build();
    }

    @Override
    public void gpsLocationOutsideOfControlGeofence(PcChilddeviceDomain.GpsLocationOutsideOfControlGeofence event) {
        log.info("[{}] gpsLocationOutsideOfControlGeofence: {}",entityId,event);
        state = state.toBuilder()
               .setCurrentGeofenceId(null)
               .build();
    }
    
    @Override
    public void gpsLocationBackToControlGeofence(PcChilddeviceDomain.GpsLocationBackToControlGeofence event) {
        log.info("[{}] gpsLocationBackToControlGeofence: {}",entityId,event);
        state = state.toBuilder()
                .setCurrentGeofenceId(event.getGeofenceId())
                .build();
    }

    @Override
    public void gpsLocationChangedControlGeofence(PcChilddeviceDomain.GpsLocationChangedControlGeofence event) {
        log.info("[{}] gpsLocationChangedControlGeofence: {}",entityId,event);
        state = state.toBuilder()
                .setCurrentGeofenceId(event.getNewGeofenceId())
                .build();
    }

    private PcChilddeviceDomain.ChildDeviceState convert(PcChildDeviceApi.ChildDevice state){
        if(state == null)
            return null;
        return PcChilddeviceDomain.ChildDeviceState.newBuilder()
                .addAllControlGeofence(state.getControlGeofenceList().stream().map(this::convert).collect(Collectors.toList()))
                .addAllParentId(state.getParentIdList())
                .build();
    }
    private PcChilddeviceDomain.ControlGeofenceState convert(PcChildDeviceApi.ControlGeofence state){
        if(state == null)
            return null;
        return PcChilddeviceDomain.ControlGeofenceState.newBuilder()
                .setGeofenceId(state.getGeofenceId())
                .setGeofencePolygon(state.getGeofencePolygon())
                .build();
    }

    private PcChildDeviceApi.ChildDevice convert(PcChilddeviceDomain.ChildDeviceState state){
        if(state == null)
            return null;
        return PcChildDeviceApi.ChildDevice.newBuilder()
                .addAllControlGeofence(state.getControlGeofenceList().stream().map(this::convert).collect(Collectors.toList()))
                .addAllParentId(state.getParentIdList())
                .build();
    }
    private PcChildDeviceApi.ControlGeofence convert(PcChilddeviceDomain.ControlGeofenceState state){
        if(state == null)
            return null;
        return PcChildDeviceApi.ControlGeofence.newBuilder()
                .setGeofenceId(state.getGeofenceId())
                .setGeofencePolygon(state.getGeofencePolygon())
                .build();
    }

    private Optional<String> isPointInPolygon(String longitude, String latitude, List<PcChildDeviceApi.ControlGeofence> geofences){
        //TODO implement pointInPolygonFunction
        return geofences.stream().findFirst().map(PcChildDeviceApi.ControlGeofence::getGeofenceId);
    }

    private boolean validateControlGeofencePolygon(String polygon){
        if(polygon==null)
            return false;
        //TODO check polygon points and crossings
        return true;
    }
}