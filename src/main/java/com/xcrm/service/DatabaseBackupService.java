package com.xcrm.service;

import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

@Log4j2
@Service
public class DatabaseBackupService {

    private final EntityManager entityManager;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${mysqldump.path}")
    private String mysqldumpPath;

    public DatabaseBackupService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public File createBackup() throws IOException, InterruptedException {
        String dbUrl = getDatabaseUrlFromEntityManager();
        String dbName = extractDbName(dbUrl);
        String fileName = "backup_" + dbName + "_" + System.currentTimeMillis() + ".sql";
        File backupFile = new File(System.getProperty("java.io.tmpdir"), fileName);

        log.info("Creando respaldo para DB: " + dbName + " con mysqldump en ruta: " + mysqldumpPath);

        ProcessBuilder pb = new ProcessBuilder(
                mysqldumpPath,
                "-u" + dbUser,
                "-p" + dbPassword,
                dbName,
                "-r",
                backupFile.getAbsolutePath()
        );

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0 || !backupFile.exists()) {
            log.error("Error al crear el respaldo de la base de datos. Código de salida: " + exitCode);
            throw new RuntimeException("Error al crear el respaldo de la base de datos.");
        }

        log.info("Respaldo creado exitosamente: " + backupFile.getAbsolutePath());
        return backupFile;
    }

    private String getDatabaseUrlFromEntityManager() {
        try {
            Session session = entityManager.unwrap(Session.class);
            Connection connection = session.doReturningWork(conn -> conn);
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener la URL de conexión desde EntityManager", e);
        }
    }

    private String extractDbName(String jdbcUrl) {
        String urlSinParametros = jdbcUrl.split("\\?")[0];
        return urlSinParametros.substring(urlSinParametros.lastIndexOf("/") + 1);
    }
}
