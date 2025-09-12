package app.pooi.workflow.infrastructure.persistence.service.impl.tenant;

import app.pooi.workflow.infrastructure.persistence.entity.tenant.TenantInfoEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.tenant.TenantInfoMapper;
import app.pooi.workflow.infrastructure.persistence.service.tenant.TenantInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

@Component
class TenantInfoServiceImpl extends ServiceImpl<TenantInfoMapper, TenantInfoEntity> implements TenantInfoService {

    @Override
    public Page<TenantInfoEntity> selectPage(Page<TenantInfoEntity> page) {
        return this.baseMapper.selectPage(page, lambdaQuery());
    }
}
