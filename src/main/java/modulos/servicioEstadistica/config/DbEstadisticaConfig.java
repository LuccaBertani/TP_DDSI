package modulos.servicioEstadistica.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
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
        basePackages = "modulos.servicioEstadistica.repositories",
        entityManagerFactoryRef = "estadisticaEntityManagerFactory",
        transactionManagerRef = "estadisticaTransactionManager"
)
public class DbEstadisticaConfig {

    @Bean(name = "estadisticaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db5")
    public DataSource estadisticaDataSource() { return DataSourceBuilder.create().build(); }

    @Bean(name = "estadisticaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean estadisticaEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("estadisticaDataSource") DataSource ds) {
        return builder.dataSource(ds)
                .packages("modulos.servicioEstadistica.entities")
                .persistenceUnit("db5").build();
    }

    @Bean(name = "estadisticaTransactionManager")
    public PlatformTransactionManager estadisticaTransactionManager(
            @Qualifier("estadisticaEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}


