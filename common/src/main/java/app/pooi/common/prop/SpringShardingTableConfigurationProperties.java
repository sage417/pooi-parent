package app.pooi.common.prop;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRuleConfiguration;
import org.apache.shardingsphere.infra.yaml.config.pojo.mode.YamlModeConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

@Data
@ConfigurationProperties(value = "common.sharding")
public class SpringShardingTableConfigurationProperties {

    private Map<String, SpringYamlRootConfigurationProperties> rootConfiguration;

    public static YamlRootConfiguration convertToYamlRootConfiguration(SpringYamlRootConfigurationProperties properties) {
        var yamlRootConfiguration = new YamlRootConfiguration();
        yamlRootConfiguration.setDatabaseName(properties.getDatabaseName());
        yamlRootConfiguration.setSchemaName(properties.getSchemaName());
        yamlRootConfiguration.setMode(properties.getMode());
        yamlRootConfiguration.setProps(properties.getProps());

        var yamlRuleConfigurations = new LinkedList<YamlRuleConfiguration>(properties.getShardingRules());
        yamlRootConfiguration.setRules(yamlRuleConfigurations);
        return yamlRootConfiguration;
    }

    @Data
    public static class SpringYamlRootConfigurationProperties {

        private String databaseName;

        private String schemaName;

        private Map<String, Map<String, Object>> dataSources = new HashMap<>();

        private Collection<YamlShardingRuleConfiguration> shardingRules = new LinkedList<>();

        private YamlModeConfiguration mode;

        private Properties props = new Properties();

        public String getDatabaseName() {
            return StringUtils.defaultIfBlank(databaseName, schemaName);
        }


    }
}
