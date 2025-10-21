package app.pooi.workflow.infrastructure.persistence.mapper.workflow.procquery;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.procquery.UserCompletedProcessTaskQuery;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.procquery.UserCompletedProcessTaskResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProcQueryMapper {

    IPage<UserCompletedProcessTaskResult> selectUserCompletedProcessTaskIds(IPage<UserCompletedProcessTaskResult> page,
                                                                            @Param("query") UserCompletedProcessTaskQuery query);
}
