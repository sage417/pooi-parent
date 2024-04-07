package app.pooi.workflow.repository.tenant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TenantRepository extends IService<TenantInfoDO> {

    Page<TenantInfoDO> selectPage(Page<TenantInfoDO> page);
}
