package com.xcrm.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseRepository {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    public void createDatabase(String dbName){

        String dbNameCreated = "xcrm_"+dbName.toLowerCase().replaceAll("\\s+","_");
        jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS "+ dbNameCreated);
        System.out.println("Vamos a crear la base de: " +dbName+" Con el nombre de :"+dbNameCreated);

    }

    public void createTables(String dbName){
        jdbcTemplate.execute("USE " + dbName);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXIST BLA BLA BLA" + dbName);

    }
}
