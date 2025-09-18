package app.pooi.workflow.infrastructure.config.grpc;

import io.grpc.NameResolverProvider;
import io.grpc.NameResolverRegistry;
import net.devh.boot.grpc.client.nameresolver.DiscoveryClientResolverFactory;
import net.devh.boot.grpc.client.nameresolver.NameResolverRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * init NameResolverRegistration because original autoconfiguration init it lazy
 *
 * @see net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
class GrpcDiscoveryClientAutoConfiguration {

    @Bean
    DiscoveryClientResolverFactory grpcDiscoveryClientResolverFactory(final DiscoveryClient client) {
        return new DiscoveryClientResolverFactory(client);
    }

    @Bean
    NameResolverRegistration grpcNameResolverRegistration(
            @Autowired(required = false) final List<NameResolverProvider> nameResolverProviders) {
        final NameResolverRegistration nameResolverRegistration = new NameResolverRegistration(nameResolverProviders);
        nameResolverRegistration.register(NameResolverRegistry.getDefaultRegistry());
        return nameResolverRegistration;
    }

}
