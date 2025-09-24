package modulos.agregacion.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "modulos.agregacion.repositories.DbProxy",
        entityManagerFactoryRef = "proxyEntityManagerFactory",
        transactionManagerRef = "proxyTransactionManager"
)
public class DbHechosProxyConfig {

    @Bean(name = "proxyDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db3")
    public DataSource proxyDataSource() { return DataSourceBuilder.create().build(); }

    @Bean(name = "proxyEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean proxyEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("proxyDataSource") DataSource ds) {
        return builder.dataSource(ds)
                .packages("modulos.agregacion.entities.DbProxy","modulos.agregacion.entities.atributosHecho")
                .persistenceUnit("db3").build();
    }

    @Bean(name = "proxyTransactionManager")
    public PlatformTransactionManager proxyTransactionManager(
            @Qualifier("proxyEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
