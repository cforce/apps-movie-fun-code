package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.MoviesBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@EnableJpaRepositories
@Configuration
public class DbConfig {

    @Bean(name="albumsDataSource")
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariDataSource hik = new HikariDataSource();
        hik.setDataSource(dataSource);
        return hik;
    }

    @Bean(name="moviesDataSource")
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariDataSource hik = new HikariDataSource();
        hik.setDataSource(dataSource);
        return hik;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean(name="entityManagerFactoryAlbums")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryAlbums(@Qualifier("albumsDataSource") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan(AlbumsBean.class.getPackage().getName());
        lef.setPersistenceUnitName("albums");
        return lef;
    }
    @Bean(name="entityManagerFactoryMovies")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryMovies(@Qualifier("moviesDataSource") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan(MoviesBean.class.getPackage().getName() );
        lef.setPersistenceUnitName("movies");
        return lef ;
    }

    @Primary
    @Bean(name = "transactionManagerMovies")
    public PlatformTransactionManager transactionManagerMovies(
            @Qualifier("entityManagerFactoryMovies") LocalContainerEntityManagerFactoryBean
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean(name = "transactionManagerAlbums")
    public PlatformTransactionManager transactionManagerAlbums(
            @Qualifier("entityManagerFactoryAlbums") LocalContainerEntityManagerFactoryBean
                    entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
