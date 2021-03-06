package com.usbus.dal.dao;

import com.usbus.commons.enums.BusStatus;
import com.usbus.dal.GenericPersistence;
import com.usbus.dal.MongoDB;
import com.usbus.dal.model.Bus;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * Created by Lufasoch on 28/05/2016.
 */
public class BusDAO {
    private final Datastore ds;
    private final GenericPersistence dao;

    public BusDAO() {
        ds = MongoDB.instance().getDatabase();
        dao = new GenericPersistence();
    }

    public String persist(Bus bus) {
        if(bus != null) {
            return dao.persist(bus);
        } else {
            return null;
        }
    }

    public Bus getById(String id) {
        if(id == null || id.isEmpty()) {
            return null;
        } else {
            return dao.get(Bus.class, id);
        }
    }

    public long countAll() {
        return dao.count(Bus.class);
    }

    public long countTenant(long tenantId) {
        if (tenantId > 0) {
            Query<Bus> query = ds.createQuery(Bus.class);
            query.criteria("tenantId").equal(tenantId);
            return query.countAll();
        } else {
            return 0;
        }
    }

    public Bus getByLocalId(long tenantId, String busId) {
        if (!((tenantId > 0) || (busId != null && !busId.isEmpty()))) {
            return null;
        } else {
            Query<Bus> query = ds.createQuery(Bus.class);
            query.and(query.criteria("id").equal(busId), query.criteria("tenantId").equal(tenantId));
            return query.get();
        }
    }

    public List<Bus> BusesByTenantIdAndStatus(long tenantId, BusStatus status, int offset, int limit){
        if(!(tenantId > 0) || (status == null)){
            return null;
        } else {
            Query<Bus> query = ds.createQuery(Bus.class);
            query.and(query.criteria("tenantId").equal(tenantId), query.criteria("status").equal(status));
            return query.offset(offset).limit(limit).asList();
        }
    }
    public List<Bus> BusesByTenantId(long tenantId, boolean active, int offset, int limit){
        if(!(tenantId > 0)){
            return null;
        } else {
            Query<Bus> query = ds.createQuery(Bus.class);
            query.and(query.criteria("tenantId").equal(tenantId), query.criteria("active").equal(active));
            return query.offset(offset).limit(limit).asList();
        }
    }

    public void remove(String id) {
        dao.remove(Bus.class, id);
    }

    public void setInactive(long tenantId, String busId) {
        if(tenantId < 0 || busId == null || busId.isEmpty()){
        } else {
            Query<Bus> query = ds.createQuery(Bus.class);

            query.and(query.criteria("id").equal(busId),
                    query.criteria("tenantId").equal(tenantId));
            UpdateOperations<Bus> updateOp = ds.createUpdateOperations(Bus.class).set("active", false).set("status", BusStatus.INACTIVE);
            ds.update(query, updateOp);
        }
    }

    public void setActive(long tenantId, String busId) {
        if (!((tenantId > 0) || (busId != null && !busId.isEmpty()))) {
        } else {
            Query<Bus> query = ds.createQuery(Bus.class);

            query.and(query.criteria("id").equal(busId),
                    query.criteria("tenantId").equal(tenantId));
            UpdateOperations<Bus> updateOp = ds.createUpdateOperations(Bus.class).set("active", true).set("status", BusStatus.ACTIVE);
            ds.update(query, updateOp);
        }
    }

    public void clean(){
        ds.delete(ds.createQuery(Bus.class));
    }
}
