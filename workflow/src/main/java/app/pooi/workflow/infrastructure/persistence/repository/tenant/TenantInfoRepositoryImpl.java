package app.pooi.workflow.infrastructure.persistence.repository.tenant;

import app.pooi.workflow.domain.model.tenant.TenantInfo;
import app.pooi.workflow.domain.repository.TenantInfoRepository;
import app.pooi.workflow.infrastructure.persistence.converter.tenant.TenantInfoConverter;
import app.pooi.workflow.infrastructure.persistence.service.tenant.TenantInfoEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
class TenantInfoRepositoryImpl implements TenantInfoRepository {

    @Resource
    private TenantInfoEntityService tenantInfoService;

    @Resource
    private TenantInfoConverter converter;


    @Override
    public List<TenantInfo> list() {
        return tenantInfoService.list().stream()
                .map(converter::toModel).collect(Collectors.toList());
    }
}
