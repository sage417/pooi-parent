package app.pooi.workflow.repository.domain.tenant;

import app.pooi.workflow.repository.tenant.TenantInfoDO;
import app.pooi.workflow.repository.tenant.TenantInfoRepository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
class TenantInfoRepositoryImpl extends ServiceImpl<TenantInfoMapper, TenantInfoDO> implements TenantInfoRepository {

    @Override
    public Page<TenantInfoDO> selectPage(Page<TenantInfoDO> page) {
        return this.baseMapper.selectPage(page, lambdaQuery());
    }
}
