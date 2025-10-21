package app.pooi.workflow.infrastructure.persistence.service.tenant;

import app.pooi.workflow.infrastructure.persistence.entity.tenant.TenantInfoEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TenantInfoEntityService extends IService<TenantInfoEntity> {

    Page<TenantInfoEntity> selectPage(Page<TenantInfoEntity> page);
}
