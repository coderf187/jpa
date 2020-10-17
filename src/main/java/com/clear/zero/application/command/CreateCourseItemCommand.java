package com.clear.zero.application.command;

import com.clear.zero.domain.model.courseitem.StudyDuration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CreateCourseItemCommand {
    private String name;
    private Integer categoryId;
    private String currencyCode;
    private BigDecimal price;
    private String remark;
    private StudyDuration studyDuration;

}
