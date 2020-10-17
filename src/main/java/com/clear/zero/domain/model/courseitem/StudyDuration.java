package com.clear.zero.domain.model.courseitem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 学籍期
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyDuration implements Serializable {
    @Column(name = "study_type", nullable = false)
    private Integer studyType;
    @Column(name = "period")
    private Integer period;
    @Column(name = "deadline")
    private Date deadline;

    public static StudyDuration of(Integer studyType, Integer period, Date deadline) {
        checkArgument(studyType != null, "学籍类型不能为空");
        checkArgument(studyType > 0, "学籍类型必须大于0");
        // todo  studyType定义成枚举类型
        return new StudyDuration(studyType, period, deadline);
    }
}
