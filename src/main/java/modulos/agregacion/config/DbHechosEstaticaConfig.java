package modulos.agregacion.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
// NO existe uno de jakarta/javax con ese nombre

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
        basePackages = "modulos.agregacion.repositories.DbEstatica",
        entityManagerFactoryRef = "estaticaEntityManagerFactory",
        transactionManagerRef = "estaticaTransactionManager"
)
public class DbHechosEstaticaConfig {

    @Bean(name = "estaticaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource estaticaDataSource() { return DataSourceBuilder.create().build(); }

    @Bean(name = "estaticaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean estaticaEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("estaticaDataSource") DataSource ds) {
        return builder.dataSource(ds)
                .packages("modulos.agregacion.entities.DbEstatica","modulos.agregacion.entities.atributosHecho")
                .persistenceUnit("db1").build();
    }

    @Bean(name = "estaticaTransactionManager")
    public PlatformTransactionManager estaticaTransactionManager(
            @Qualifier("estaticaEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
