package com.clear.zero.ui.payload;

import com.clear.zero.application.command.CreateCourseItemCommand;
import com.clear.zero.domain.model.courseitem.StudyDuration;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreateCourseItemPayload {
    private String name;
    private Integer categoryId;
    private String currencyCode;
    private BigDecimal price;
    private String remark;
    private Integer studyType;
    private Integer period;
    private Date deadline;

    public CreateCourseItemCommand toCommand() {
        StudyDuration studyDuration = StudyDuration.of(studyType, period, deadline);
        return new CreateCourseItemCommand(name, categoryId, currencyCode, price, remark, studyDuration);
    }

}
