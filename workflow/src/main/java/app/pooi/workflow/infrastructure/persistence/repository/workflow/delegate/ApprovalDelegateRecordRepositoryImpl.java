package app.pooi.workflow.infrastructure.persistence.repository.workflow.delegate;

import app.pooi.workflow.domain.repository.ApprovalDelegateRecordRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.delegate.ApprovalDelegateRecordConverter;
import app.pooi.workflow.infrastructure.persistence.service.workflow.delegate.ApprovalDelegateRecordEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ApprovalDelegateRecordRepositoryImpl implements ApprovalDelegateRecordRepository {

    @Resource
    private ApprovalDelegateRecordEntityService approvalDelegateRecordEntityService;

    @Resource
    private ApprovalDelegateRecordConverter approvalDelegateRecordConverter;

}
