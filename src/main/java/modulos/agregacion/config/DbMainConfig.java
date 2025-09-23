package modulos.agregacion.config;

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
        basePackages = "modulos.agregacion.repositories.DbMain",
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "mainTransactionManager"
)
public class DbMainConfig {

        @Primary
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.db4")
        public DataSource mainDataSource() {
            return DataSourceBuilder.create().build();
        }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mainDataSource())
                .packages("modulos.agregacion.entities.DbMain", "modulos.agregacion.entities.atributosHecho")  // Ajustar si cambia tu ruta
                .persistenceUnit("db4")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager mainTransactionManager(@Qualifier("mainEntityManagerFactory") EntityManagerFactory emf) {
            return new JpaTransactionManager(emf);
        }
}

