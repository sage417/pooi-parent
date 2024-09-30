package app.pooi.workflow.configuration.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Properties;

public class DataBaseShardingAlgorithm implements StandardShardingAlgorithm<String> {

    @Override
    public void init(Properties props) {
    }

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> preciseShardingValue) {
        return "ds1";
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> rangeShardingValue) {
        return availableTargetNames;
    }
}
