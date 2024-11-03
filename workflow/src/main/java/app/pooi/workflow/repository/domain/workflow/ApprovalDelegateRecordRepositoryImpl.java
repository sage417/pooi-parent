package app.pooi.workflow.repository.domain.workflow;

import app.pooi.workflow.repository.workflow.ApprovalDelegateRecordDO;
import app.pooi.workflow.repository.workflow.ApprovalDelegateRecordRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ApprovalDelegateRecordRepositoryImpl extends ServiceImpl<ApprovalDelegateRecordMapper, ApprovalDelegateRecordDO> implements ApprovalDelegateRecordRepository {
}
