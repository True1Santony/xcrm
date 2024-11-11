package com.xcrm.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Repository
@Transactional
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

        dbName="xcrm_"+dbName.toLowerCase().replaceAll("\\s+","_");

        jdbcTemplate.execute("USE "+dbName);

        // Lee el archivo SQL de los recursos
        String createTablesScript = loadSqlScript("db/authorities.sql");

        jdbcTemplate.execute(createTablesScript);

        createTablesScript = loadSqlScript("db/organizaciones.sql");

        jdbcTemplate.execute(createTablesScript);

        createTablesScript = loadSqlScript("db/users.sql");

        jdbcTemplate.execute(createTablesScript);

    }

    private String loadSqlScript(String filename) {
        StringBuilder script = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(new ClassPathResource(filename).getFile(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo SQL: " + filename, e);
        }
        return script.toString();
    }

    @Transactional
    public void insertarOrganizacionEnBaseDeDatos(String dbName, Long organizacionId, String email, String plan) {


        String dbNameName = "xcrm_" + dbName.toLowerCase().replaceAll("\\s+", "_");
        jdbcTemplate.execute("USE " + dbNameName);

        // Insertar datos de la organización en la tabla `organizaciones`
        String sqlInsert = "INSERT INTO organizaciones (id, nombre, email, plan, creado) VALUES (?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sqlInsert, organizacionId, dbName, email, plan);
    }

    @Transactional
    public void insertarUsuarioEnBaseDeDatos(String dbName, Long organizacionId, String username, String password) {
        dbName = "xcrm_" + dbName.toLowerCase().replaceAll("\\s+", "_");
        jdbcTemplate.execute("USE " + dbName);

        // Insertar el usuario administrador en la tabla `users`
        String sqlInsertAdmin = "INSERT INTO users (username, password, enabled, organizacion_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertAdmin, username, password, 1, organizacionId);
    }

    @Transactional
    public void insertarRolDeUsuarioEnBaseDeDatos(String dbName, String username, String role) {
        dbName = "xcrm_" + dbName.toLowerCase().replaceAll("\\s+", "_");
        jdbcTemplate.execute("USE " + dbName);

        // Insertar el rol de administrador para el usuario en la tabla `authorities`
        String sqlInsertAuthority = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertAuthority, username, role);
    }
}
