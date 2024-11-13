package com.xcrm.utils;

import com.xcrm.service.OrganizacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomRoutingDataSource extends AbstractRoutingDataSource {
    @Autowired
    private HttpSession httpSession;

    @Autowired
    @Lazy
    private OrganizacionService organizacionService;

    private Map<String, DataSource> dataSources = new HashMap<>();

    private DataSource defaultDataSource; // Almacena la fuente de datos predeterminada


    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;



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
            if ("mi_app".equals(tenantId)) {
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
        if (!dataSources.containsKey(tenantId)) {
            // Si no existe, crear un nuevo DataSource y almacenarlo en el caché
            String dbUrl = "jdbc:mysql://192.168.1.36:3306/xcrm_" + tenantId;

            DataSource newDataSource = DataSourceBuilder.create()
                    .url(dbUrl)
                    .username(username) // Cambia según tu configuración
                    .password(password) // Cambia según tu configuración
                    .driverClassName(driverClassName)
                    .build();
            dataSources.put(tenantId, newDataSource);
        }

        // Retornar el DataSource correspondiente al tenantId
        return dataSources.get(tenantId);
    }

   /* @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = (String) determineCurrentLookupKey();
        return (DataSource) dataSources.getOrDefault(tenantId, defaultDataSource);
    }*/

    public void resetToDefaultDataSource() {
        httpSession.removeAttribute("TENANT_ID"); // Elimina el TENANT_ID para revertir al default
    }

    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("default", defaultDataSource); // Añade tu DataSource por defecto

        // Aquí podrías añadir lógicas para inicializar otras fuentes de datos
        // targetDataSources.put("otroTenant", otroDataSource);

        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

}
