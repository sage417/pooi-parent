/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.delegate;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.ApprovalDelegateConfigEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.delegate.ApprovalDelegateConfigEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.delegate.ApprovalDelegateConfigEntityService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApprovalDelegateConfigServiceImpl extends ServiceImpl<ApprovalDelegateConfigEntityMapper, ApprovalDelegateConfigEntity> implements ApprovalDelegateConfigEntityService {

    @Override
    public List<ApprovalDelegateConfigEntity> selectValidByProcessDefinitionKeyAndTenantId(String definitionKey, String tenantId) {
        return baseMapper.selectList(Wrappers.lambdaQuery(ApprovalDelegateConfigEntity.class)
                .eq(ApprovalDelegateConfigEntity::getProcessDefinitionKey, definitionKey)
                .eq(ApprovalDelegateConfigEntity::getTenantId, tenantId)
                .gt(ApprovalDelegateConfigEntity::getType, 0));
    }
}
