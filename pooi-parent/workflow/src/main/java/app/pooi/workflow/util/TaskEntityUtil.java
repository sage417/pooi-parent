/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.TaskInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskEntityUtil {


    public static Set<String> getAssigneeAndCandidates(TaskInfo task) {
        Set<String> users = new HashSet<>();

        if (StringUtils.isNotEmpty(task.getAssignee())) {
            users.add(task.getAssignee());
        }

        task.getIdentityLinks().stream()
                .filter(l -> StringUtils.equalsAny(l.getType(), IdentityLinkType.ASSIGNEE, IdentityLinkType.CANDIDATE))
                .map(IdentityLinkInfo::getUserId)
                .filter(StringUtils::isNotEmpty)
                .forEach(users::add);

        return users;
    }

    public static Set<String> getCandidates(TaskInfo task) {
        return task.getIdentityLinks().stream()
                .filter(l -> StringUtils.equals(l.getType(), IdentityLinkType.CANDIDATE))
                .map(IdentityLinkInfo::getUserId)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }
}
