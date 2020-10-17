package com.clear.zero.application;

import com.clear.zero.application.command.CreateCourseItemCommand;
import com.clear.zero.domain.model.courseitem.CourseItem;

public interface CourseItemService {
    CourseItem createCourseItem(CreateCourseItemCommand command);

}
