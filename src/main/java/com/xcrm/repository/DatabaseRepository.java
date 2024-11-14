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

        jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS "+ dbName);
        System.out.println("Vamos a crear la base : " +dbName);

    }

    public void createTables(String dbName){

        jdbcTemplate.execute("USE "+dbName);

        // Lee el archivo SQL de los recursos
        String createTablesScript = loadSqlScript("db/authorities.sql");

        jdbcTemplate.execute(createTablesScript);
        System.out.println("tabla 1 creada");

        createTablesScript = loadSqlScript("db/organizaciones.sql");

        jdbcTemplate.execute(createTablesScript);
        System.out.println("tabla 2 creada");


        createTablesScript = loadSqlScript("db/users.sql");

        jdbcTemplate.execute(createTablesScript);
        System.out.println("tabla 3 creada");

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
    public void insertarOrganizacionEnBaseDeDatos(String nombre, Long organizacionId, String email, String plan, String nombreDB) {



        jdbcTemplate.execute("USE " + nombreDB);

        // Insertar datos de la organización en la tabla `organizaciones`
        String sqlInsert = "INSERT INTO organizaciones (id, nombre, email, plan, nombreDB, creado) VALUES (?, ?, ?, ?,?, NOW())";
        jdbcTemplate.update(sqlInsert, organizacionId, nombre, email, plan, nombreDB);
        System.out.println("insertando a "+nombre+" en la base de datos central");
    }

    @Transactional
    public void insertarUsuarioEnBaseDeDatos(String dbName, Long organizacionId, String username, String password) {
        jdbcTemplate.execute("USE " + dbName);

        // Insertar el usuario administrador en la tabla `users`
        String sqlInsertAdmin = "INSERT INTO users (username, password, enabled, organizacion_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertAdmin, username, password, 1, organizacionId);
        System.out.println("insertando a "+username+" en la base de datos central");
    }

    @Transactional
    public void insertarRolDeUsuarioEnBaseDeDatos(String dbName, String username, String role) {
        jdbcTemplate.execute("USE " + dbName);

        // Insertar el rol de administrador para el usuario en la tabla `authorities`
        String sqlInsertAuthority = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertAuthority, username, role);
    }
}
