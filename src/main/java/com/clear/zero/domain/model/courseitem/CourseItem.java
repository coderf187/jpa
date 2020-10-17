package com.clear.zero.domain.model.courseitem;

import com.clear.zero.application.command.CreateCourseItemCommand;
import com.clear.zero.domain.common.model.AuditEntity;
import com.clear.zero.domain.common.model.Price;
import com.google.common.base.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 课程明细
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@GenericGenerator(name = "CustomID", strategy = "com.clear.zero.util.CustomIDGenerator")
public class CourseItem extends AuditEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "CustomID")
    private Long id;
    @Convert(converter = CourseItemNumberConverter.class)
    @Column(name = "item_no", length = 32, nullable = false, unique = true)
    private CourseItemNumber itemNo;
    @Column(name = "name", length = 64, nullable = false)
    private String name;
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    @Embedded
    private Price price;
    @Column(name = "remark", length = 256)
    private String remark;
    @Embedded
    private StudyDuration studyDuration;

    public static CourseItem of(CreateCourseItemCommand command) {
        checkArgument(!StringUtils.isEmpty(command.getName()), "课程名称不能为空");
        Integer categoryId = command.getCategoryId();
        checkArgument(categoryId != null, "课程类目不能为空");
        checkArgument(command.getStudyDuration() != null, "学籍不能为空");
        Price price = Price.of(command.getCurrencyCode(), command.getPrice());
        CourseItemNumber newItemNo = CourseItemNumber.of(categoryId);
        CourseItem courseItem = new CourseItem(null, newItemNo, command.getName(), categoryId, price, command.getRemark(), command.getStudyDuration());
        courseItem.setCreatedBy(1);
        courseItem.setUpdatedBy(1);
        return courseItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CourseItem that = (CourseItem) o;
        return Objects.equal(itemNo, that.itemNo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), itemNo);
    }
}
