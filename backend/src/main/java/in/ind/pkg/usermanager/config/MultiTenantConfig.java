package in.ind.pkg.usermanager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MultiTenantConfig implements WebMvcConfigurer {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, DataSource> TENANT_DATA_SOURCES = new ConcurrentHashMap<>();

    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    public static DataSource createDataSource(String tenantId) {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:" + tenantId + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return getCurrentTenant();
            }

            @Override
            protected DataSource determineTargetDataSource() {
                String tenantId = getCurrentTenant();
                if (tenantId == null || tenantId.isBlank()) {
                    throw new IllegalStateException("No tenant set in x-tenant-id header");
                }

                return TENANT_DATA_SOURCES.computeIfAbsent(tenantId, MultiTenantConfig::createDataSource);
            }
        };

        // Set default target to a dummy DataSource to comply with Spring boot requirements
        routingDataSource.setTargetDataSources(new HashMap<>());
        routingDataSource.setDefaultTargetDataSource(createDataSource("default"));
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }
}
