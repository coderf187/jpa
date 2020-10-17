package com.clear.zero.application.impl;

import com.clear.zero.application.CourseItemService;
import com.clear.zero.application.command.CreateCourseItemCommand;
import com.clear.zero.domain.model.courseitem.CourseItem;
import com.clear.zero.domain.model.courseitem.CourseItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseItemSerivceImpl implements CourseItemService {

    private CourseItemRepository courseItemRepository;

    public CourseItemSerivceImpl(CourseItemRepository courseItemRepository) {
        this.courseItemRepository = courseItemRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseItem createCourseItem(CreateCourseItemCommand command) {
        CourseItem courseItem = CourseItem.of(command);
        courseItemRepository.save(courseItem);
        return courseItem;
    }
}
