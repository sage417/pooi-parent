package app.pooi.workflow.infrastructure.persistence.converter.tenant;

import app.pooi.workflow.domain.model.tenant.TenantInfo;
import app.pooi.workflow.infrastructure.persistence.entity.tenant.TenantInfoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantInfoConverter {

    TenantInfo toModel(TenantInfoEntity entity);

    TenantInfoEntity toEntity(TenantInfo model);
}
