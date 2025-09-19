package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.delegate;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate.ApprovalDelegateRecordEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.delegate.ApprovalDelegateRecordEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.delegate.ApprovalDelegateRecordEntityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ApprovalDelegateRecordServiceImpl extends ServiceImpl<ApprovalDelegateRecordEntityMapper, ApprovalDelegateRecordEntity> implements ApprovalDelegateRecordEntityService {
}
