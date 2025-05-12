package com.xcrm.repository;

import com.xcrm.model.Authority;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Repository
@Transactional
public class DatabaseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String dbCentral;

    public DatabaseRepository(JdbcTemplate jdbcTemplate,
                              @Value("${xcrm.central-db-name}") String dbCentral){
        this.jdbcTemplate=jdbcTemplate;
        this.dbCentral = dbCentral;
    }

    public void createDatabase(String dbName){
        try {
            // Intento crear la base de datos
            jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
            log.info("Base de datos creada: " + dbName);
        } catch (Exception e) {
            log.error("Error al crear la base de datos: " + dbName);
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
            executeTableScript("db/contacto_mensaje.sql");

            // Habilitar las restricciones de claves foráneas nuevamente
            jdbcTemplate.execute("SET foreign_key_checks = 1");

            log.info("Tablas creadas exitosamente.");

        } catch (Exception e) {
            // Si ocurre un error, eliminamos la base de datos
            jdbcTemplate.execute("DROP DATABASE IF EXISTS " + dbName);
            log.error("Error al crear las tablas. Se ha eliminado la base de datos: " + dbName);
            throw new RuntimeException("Error al crear las tablas, la base de datos fue eliminada", e);
        }

    }

    private void executeTableScript(String filename) {
        String createTableScript = loadSqlScript(filename);
        jdbcTemplate.execute(createTableScript);
        log.info("Tabla creada desde el script: " + filename);
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
        log.info("insertando a "+nombre+" en su propia base de datos");
    }

    @Transactional
    public void insertarUsuarioEnBaseDeDatos(String dbName, UUID userId, Long organizacionId, String username, String password) {
        jdbcTemplate.execute("USE " + dbName);

        // Convertir UUID a byte[] asi lo espera la base de datos
        byte[] userIdBytes = uuidToBytes(userId);

        // Insertar el usuario administrador en la tabla `users`
        String sqlInsertAdmin = "INSERT INTO users (id, username, password, enabled, organizacion_id) VALUES (?,?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertAdmin,userIdBytes, username, password, 1, organizacionId);
        log.info("insertando a "+username+" en la base de datos de su organizacion");
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

    @Transactional
    public void actualizarUsuarioYRolesEnDbCentral(UUID userId,
                                                   String nuevoUsername,
                                                   String nuevoPassword,
                                                   Set<Authority> nuevasAuthorities) {

        jdbcTemplate.execute("USE " + dbCentral);

        byte[] userIdBytes = uuidToBytes(userId);

        if (nuevoPassword != null && !nuevoPassword.isEmpty()) {
            String sqlUpdateUser = "UPDATE users SET username = ?, password = ? WHERE id = ?";
            jdbcTemplate.update(sqlUpdateUser, nuevoUsername, nuevoPassword, userIdBytes);
        } else {
            String sqlUpdateUserOnlyUsername = "UPDATE users SET username = ? WHERE id = ?";
            jdbcTemplate.update(sqlUpdateUserOnlyUsername, nuevoUsername, userIdBytes);
        }

        // Eliminar roles anteriores
        String sqlDeleteRoles = "DELETE FROM authorities WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteRoles, userIdBytes);

        // Insertar nuevos roles
        String sqlInsertAuthority = "INSERT INTO authorities (id, user_id, authority) VALUES (?, ?, ?)";
        for (Authority auth : nuevasAuthorities) {
            byte[] idAuthority = uuidToBytes(auth.getId());
            jdbcTemplate.update(sqlInsertAuthority, idAuthority, userIdBytes, auth.getAuthority());
        }

        log.info("Usuario y roles actualizados en la base central.");
    }


    // Método para convertir UUID a byte[]
    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public void eliminarUsuarioYRolesDeDbCentral(UUID userId) {
        jdbcTemplate.execute("USE "+ dbCentral);

        byte[] userIdBytes = uuidToBytes(userId);

        //Elimino roles, por la dependencia de las PK y FK
        String deleteRoles = "DELETE FROM authorities WHERE user_id = ?";
        jdbcTemplate.update(deleteRoles, userIdBytes);

        // Luego elimino el usuario
        String deleteUser = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUser, userIdBytes);

        log.info("Usuario y roles eliminados en la base central.");
    }
}
