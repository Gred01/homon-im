package com.max.homon.kit.db.util;

import com.max.homon.kit.db.constant.DataSourceKey;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * spring动态数据源（需要继承AbstractRoutingDataSource）
 *
 * @author owen
 * @create 2017年7月2日
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> datasources;

    public DynamicDataSource() {
        datasources = new HashMap<>();

        super.setTargetDataSources(datasources);

    }

    public <T extends DataSource> void addDataSource(DataSourceKey key, T data) {
        datasources.put(key, data);
    }

    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDataSourceKey();
    }

}