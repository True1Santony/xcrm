package com.xcrm.configuration;

import com.xcrm.utils.CustomRoutingDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement//gestion automatica de transacciones, consistencia y atomicidad
@EnableJpaRepositories("com.xcrm.repository")//no necesario en el contexto de spring boot,spring si que lo necesita
public class DataSourceConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String baseUrl; // URL base para la conexión predeterminada

    @Bean
    @Primary
    public DataSource defaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(baseUrl); // La base de datos predeterminada
        return dataSource;
    }

    @Bean
    public DataSource routingDataSource() {
        CustomRoutingDataSource routingDataSource = new CustomRoutingDataSource(defaultDataSource());
        routingDataSource.setDefaultTargetDataSource(defaultDataSource());
        return routingDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(routingDataSource());
        emf.setPackagesToScan("com.xcrm.model"); // el paquete de las entidades a escanear
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Configuraciones adicionales de JPA/Hibernate
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.show_sql", "false"); // Muestra las consultas SQL en la consola
        jpaProperties.put("hibernate.format_sql", "true"); // Formatea las consultas para mejor legibilidad
        jpaProperties.put("hibernate.use_sql_comments", "true"); // Agrega comentarios en las consultas SQL
        jpaProperties.put("hibernate.hbm2ddl.auto", "update"); // Opcional: actualizar el esquema automáticamente

        emf.setJpaProperties(jpaProperties);
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
