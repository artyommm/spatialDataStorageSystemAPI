package com.example.spatialDataStorageSystemProject.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MainRepo {
    //@Query("SELECT type_name, ST_AsGeoJson(data) FROM geom_objects;")

}
