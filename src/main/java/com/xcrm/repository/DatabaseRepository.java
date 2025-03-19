package com.xcrm.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Transactional
public class DatabaseRepository {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    public void createDatabase(String dbName){
        try {
            // Intento crear la base de datos
            jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("Base de datos creada: " + dbName);
        } catch (Exception e) {
            System.out.println("Error al crear la base de datos: " + dbName);
            throw new RuntimeException("No se pudo crear la base de datos", e);
        }
    }

    public void createTables(String dbName){

        try {
            jdbcTemplate.execute("USE " + dbName);

            // Deshabilitar las restricciones de claves foráneas
            jdbcTemplate.execute("SET foreign_key_checks = 0");

            // Lee los scripts SQL de las tablas
            executeTableScript("db/authorities.sql");
            executeTableScript("db/organizaciones.sql");
            executeTableScript("db/users.sql");
            executeTableScript("db/clientes.sql");
            executeTableScript("db/campanias.sql");
            executeTableScript("db/comerciales_campanias.sql");
            executeTableScript("db/clientes_campanias.sql");
            executeTableScript("db/comerciales_clientes.sql");
            executeTableScript("db/interacciones.sql");
            executeTableScript("db/ventas.sql");

            // Habilitar las restricciones de claves foráneas nuevamente
            jdbcTemplate.execute("SET foreign_key_checks = 1");

            System.out.println("Tablas creadas exitosamente.");

        } catch (Exception e) {
            // Si ocurre un error, eliminamos la base de datos
            jdbcTemplate.execute("DROP DATABASE IF EXISTS " + dbName);
            System.out.println("Error al crear las tablas. Se ha eliminado la base de datos: " + dbName);
            throw new RuntimeException("Error al crear las tablas, la base de datos fue eliminada", e);
        }

    }

    private void executeTableScript(String filename) {
        String createTableScript = loadSqlScript(filename);
        jdbcTemplate.execute(createTableScript);
        System.out.println("Tabla creada desde el script: " + filename);
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
    public void insertarUsuarioEnBaseDeDatos(String dbName, UUID userId, Long organizacionId, String username, String password) {
        jdbcTemplate.execute("USE " + dbName);

        // Convertir UUID a byte[] asi lo espera la base de datos
        byte[] userIdBytes = uuidToBytes(userId);

        // Insertar el usuario administrador en la tabla `users`
        String sqlInsertAdmin = "INSERT INTO users (id, username, password, enabled, organizacion_id) VALUES (?,?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertAdmin,userIdBytes, username, password, 1, organizacionId);
        System.out.println("insertando a "+username+" en la base de datos central");
    }

    @Transactional
    public void insertarRolDeUsuarioEnBaseDeDatos(String dbName, UUID idAuthority, UUID userId, String role) {
        jdbcTemplate.execute("USE " + dbName);

        // Convertir UUID a byte[] asi lo espera la base de datos
        byte[] idAuthorityBytes = uuidToBytes(idAuthority);
        byte[] userIdBytes = uuidToBytes(userId);

        // Insertar el rol de administrador para el usuario en la tabla `authorities`
        String sqlInsertAuthority = "INSERT INTO authorities (id, user_id, authority) VALUES (?,?,?)";
        jdbcTemplate.update(sqlInsertAuthority,idAuthorityBytes , userIdBytes, role);
    }

    // Método para convertir UUID a byte[]
    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
