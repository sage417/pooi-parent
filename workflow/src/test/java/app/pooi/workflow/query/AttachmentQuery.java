/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.pooi.workflow.query;

import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.common.engine.impl.query.AbstractQuery;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.task.Attachment;

import java.io.Serial;
import java.util.List;

/**
 * @author Bassam Al-Sarori
 */
public class AttachmentQuery extends AbstractQuery<AttachmentQuery, Attachment> {

    @Serial
    private static final long serialVersionUID = 1L;
    protected String attachmentId;
    protected String attachmentName;
    protected String attachmentType;
    protected String userId;
    protected String taskId;
    protected String processInstanceId;

    public AttachmentQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    public AttachmentQuery attachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
        return this;
    }

    public AttachmentQuery attachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
        return this;
    }

    public AttachmentQuery attachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
        return this;
    }

    public AttachmentQuery userId(String userId) {
        this.userId = userId;
        return this;
    }

    public AttachmentQuery taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public AttachmentQuery processInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public AttachmentQuery orderByAttachmentId() {
        return orderBy(AttachmentQueryProperty.ATTACHMENT_ID);
    }

    public AttachmentQuery orderByAttachmentName() {
        return orderBy(AttachmentQueryProperty.NAME);
    }

    public AttachmentQuery orderByAttachmentCreateTime() {
        return orderBy(AttachmentQueryProperty.CREATE_TIME);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        return (Long) CommandContextUtil.getDbSqlSession(commandContext).selectOne("org.flowable.standalone.cfg.AttachmentMapper.selectAttachmentCountByQueryCriteria", this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Attachment> executeList(CommandContext commandContext) {
        return CommandContextUtil.getDbSqlSession(commandContext).selectList("org.flowable.standalone.cfg.AttachmentMapper.selectAttachmentByQueryCriteria", this);
    }

}