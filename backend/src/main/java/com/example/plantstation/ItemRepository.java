package com.example.plantstation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<ClusterItem, Long> {

    @Query("{name:'?0'}")
    ClusterItem findItemByName(String name);

    @Query(value="{name:'?0'}")
    List<ClusterItem> findAll(String name);

    Long deleteClusterItemByName(String name);

    public long count();

}
