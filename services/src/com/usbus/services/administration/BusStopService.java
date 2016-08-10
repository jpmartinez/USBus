package com.usbus.services.administration;

import com.usbus.bll.administration.beans.BusStopBean;

import com.usbus.commons.auxiliaryClasses.RouteStop;
import com.usbus.commons.enums.Rol;
import com.usbus.dal.model.BusStop;
import com.usbus.services.auth.Secured;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpmartinez on 05/06/16.
 */
/*El path del servicio siempre debe ser en singular y ser el nombre del recurso con el que se va a interactuar*/
@Path("{tenantId}/busstop")
public class BusStopService {
    //@EJB
    BusStopBean ejb = new BusStopBean();


    /*OBTENER LISTA siempre se debe hacer en un GET*/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured({Rol.ADMINISTRATOR, Rol.CLIENT})
    public Response getBusStopList(@PathParam("tenantId") Long tenantId,
                                   @QueryParam("query") String query,
                                   @QueryParam("status") boolean status,
                                   @QueryParam("offset") int offset,
                                   @QueryParam("limit") int limit,
                                   @QueryParam("name") String name,
                                   @QueryParam("origin") String origin,
                                   @QueryParam("destination") String destination) {
        if (query==null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<RouteStop> routeStops = new ArrayList<>();
        switch (query.toUpperCase()){
            case "ALL":
                List<BusStop> busStopList = ejb.getByTenant(tenantId, offset, limit, status,name);
                if (busStopList == null) {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
                return Response.ok(busStopList).build();
            case "ENDPOINTS":
                List<BusStop> endpointsList = ejb.getEndpointsByTenant(tenantId, offset, limit);
                if (endpointsList == null) {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
                return Response.ok(endpointsList).build();
            case "DESTINATIONS":
                routeStops = ejb.getDestinations(tenantId, offset, limit, origin);
                if (routeStops == null) {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
                return Response.ok(routeStops).build();
            case "ORIGINS":
                routeStops = ejb.getOrigins(tenantId, offset, limit, destination);
                if (routeStops == null) {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
                return Response.ok(routeStops).build();

        }

        return Response.status(Response.Status.BAD_REQUEST).build();

    }

    /*OBTENER UN ELEMENTO siempre se debe hacer en un get pasando como PATH el código del elemento*/
    @GET
    @Path("{busStopId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(Rol.ADMINISTRATOR)
    public Response getBusStop(@PathParam("tenantId") Long tenantId, @PathParam("busStopId") Long busStopId) {

        BusStop busStop = ejb.getByLocalId(tenantId, busStopId);
        if (busStop == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(busStop).build();
    }

//    @GET
//    @Path("{busStopId}/destinations")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured(Rol.ADMINISTRATOR)
//    public Response getBusStopDestinations(@PathParam("tenantId")Long tenantId, @PathParam("busStopId") Long busStopId,@QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("origin") String destination){
//    List<RouteStop> busStops = ejb.getOrigins(tenantId, offset, limit, origin);
//    if (busStops == null) {
//        return Response.status(Response.Status.NO_CONTENT).build();
//    }
//    return Response.ok(busStops).build();
//    }
//    @GET
//    @Path("{busStopId}/origins")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured(Rol.ADMINISTRATOR)
//    public Response getBusStopOrigins(@PathParam("tenantId")Long tenantId, @PathParam("busStopId") Long busStopId,@QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("origin") String origin){
//    List<RouteStop> busStops = ejb.getOrigins(tenantId, offset, limit, destination);
//    if (busStops == null) {
//        return Response.status(Response.Status.NO_CONTENT).build();
//    }
//    return Response.ok(busStops).build();

    //    }
    /*MODIFICAR UN ELEMENTO siempre se debe hacer en un PUT pasando como PATH el código del elemento*/
    @PUT
    @Path("{busStopId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(Rol.ADMINISTRATOR)
    public Response updateBusStop(@PathParam("tenantId") Long tenantId, @PathParam("busStopId") Long busStopId, BusStop busStop) {

        BusStop busStopAux = ejb.getByLocalId(tenantId, busStopId);
        busStop.set_id(busStopAux.get_id());

        String oid = ejb.persist(busStop);
        if (oid == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(ejb.getById(oid)).build();
    }


    /*CREACIÓN, siempre debe ser un POST*/
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(Rol.ADMINISTRATOR)
    public Response createBusStop(BusStop busStop) {
        busStop.setActive(true);
        String oid = ejb.persist(busStop);
        if (oid == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(ejb.getById(oid)).build();
    }

    /*ELIMINACIÓN siempre debe ser un DELETE indicando como path param el código del elemento*/
    @DELETE
    @Path("{busStopId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(Rol.ADMINISTRATOR)
    public Response removeBusStop(@PathParam("tenantId") Long tenantId, @PathParam("busStopId") Long busStopId) {
        try {
            ejb.setInactive(tenantId, busStopId); //POR AHORA SOLO IMPLEMENTAMOS UN BORRADO LÓGICO.
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }


}
