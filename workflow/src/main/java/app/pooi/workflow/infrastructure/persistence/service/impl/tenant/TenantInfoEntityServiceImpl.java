package app.pooi.workflow.infrastructure.persistence.service.impl.tenant;

import app.pooi.workflow.infrastructure.persistence.entity.tenant.TenantInfoEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.tenant.TenantInfoEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.tenant.TenantInfoEntityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

@Component
class TenantInfoEntityServiceImpl extends ServiceImpl<TenantInfoEntityMapper, TenantInfoEntity> implements TenantInfoEntityService {

    @Override
    public Page<TenantInfoEntity> selectPage(Page<TenantInfoEntity> page) {
        return this.baseMapper.selectPage(page, lambdaQuery());
    }
}
