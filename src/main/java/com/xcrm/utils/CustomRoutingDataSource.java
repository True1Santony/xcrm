package com.xcrm.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    private Map<String, DataSource> dataSources = new HashMap<>();

    private DataSource defaultDataSource; // Almacena la fuente de datos predeterminada

    @Autowired
    private HttpSession httpSession;

    @Value("${database.url.prefix}")
    private String dbUrlPrefix;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${xcrm.central-db-name}")
    private String databaseCentralName;

    public CustomRoutingDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource; // Inicializa el DataSource predeterminado
    }

    @Override
    protected Object determineCurrentLookupKey() {
        HttpSession session = RequestContextHolder.getRequestAttributes() != null
                ? ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession(false)
                : null;

        if (session != null) {
            String tenantId = (String) session.getAttribute("TENANT_ID");

            // Si el tenantId es "mi_app", reemplázalo por "default"
            if (databaseCentralName.equals(tenantId)) {
                return "default";
            }

            return tenantId;
        } else {
            return "default"; // Usa el DataSource predeterminado si no hay sesión
        }
    }

   @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = (String) determineCurrentLookupKey();


       // Si el tenantId es "default", utiliza el DataSource predeterminado sin crear uno nuevo
       if (tenantId == null || tenantId.equals("default")) {
           // Si el tenantId es nulo o es "default", utiliza el DataSource predeterminado
           return defaultDataSource;
       }

       // Convertir el tenantId a minúsculas
       tenantId = tenantId.toLowerCase().replaceAll("\\s+", "_");

        // Verifica si ya tenemos un DataSource en caché para esta organización
       synchronized (this) {//condicion de carrera
           if (!dataSources.containsKey(tenantId)) {
               // Si no existe, crear un nuevo DataSource y almacenarlo en el caché
               String dbUrl = dbUrlPrefix + tenantId;

               DataSource newDataSource = DataSourceBuilder.create()
                       .url(dbUrl)
                       .username(username)
                       .password(password)
                       .driverClassName(driverClassName)
                       .build();
               dataSources.put(tenantId, newDataSource);
           }
       }

        // Retornar el DataSource correspondiente al tenantId
        return dataSources.get(tenantId);
    }

    public void resetToDefaultDataSource() {
        httpSession.removeAttribute("TENANT_ID"); // Elimina el TENANT_ID para revertir al default
    }

    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("default", defaultDataSource); // Añade DataSource por defecto
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

}
