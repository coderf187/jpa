package com.clear.zero.domain.model.courseitem;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CourseItemNumberConverter implements AttributeConverter<CourseItemNumber, String> {
    @Override
    public String convertToDatabaseColumn(CourseItemNumber courseItemNumber) {
        return courseItemNumber.getValue();
    }

    @Override
    public CourseItemNumber convertToEntityAttribute(String value) {
        return CourseItemNumber.of(value);
    }
}
