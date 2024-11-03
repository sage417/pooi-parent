/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.repository.domain.workflow;

import app.pooi.workflow.repository.workflow.ApprovalDelegateConfigDO;
import app.pooi.workflow.repository.workflow.ApprovalDelegateConfigRepository;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApprovalDelegateConfigRepositoryImpl extends ServiceImpl<ApprovalDelegateConfigMapper, ApprovalDelegateConfigDO> implements ApprovalDelegateConfigRepository {

    @Override
    public List<ApprovalDelegateConfigDO> selectValidByProcessDefinitionKeyAndTenantId(String definitionKey, String tenantId) {
        return baseMapper.selectList(Wrappers.lambdaQuery(ApprovalDelegateConfigDO.class)
                .eq(ApprovalDelegateConfigDO::getProcessDefinitionKey, definitionKey)
                .eq(ApprovalDelegateConfigDO::getTenantId, tenantId)
                .gt(ApprovalDelegateConfigDO::getType, 0));
    }
}
