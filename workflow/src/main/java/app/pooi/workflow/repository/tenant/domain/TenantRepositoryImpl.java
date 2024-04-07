package app.pooi.workflow.repository.tenant.domain;

import app.pooi.workflow.repository.tenant.TenantInfoDO;
import app.pooi.workflow.repository.tenant.TenantRepository;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
class TenantRepositoryImpl extends ServiceImpl<TenantInfoMapper, TenantInfoDO> implements TenantRepository {

    @Override
    public Page<TenantInfoDO> selectPage(Page<TenantInfoDO> page) {
        return this.baseMapper.selectPage(page, Wrappers.lambdaQuery(TenantInfoDO.class));
    }
}
