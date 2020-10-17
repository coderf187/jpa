package com.clear.zero.domain.model.courseitem;

import java.util.List;

public interface CourseItemRepository {
    CourseItem findByItemNo(CourseItemNumber itemNo);

    List<CourseItem> findByItemNos(List<CourseItemNumber> itemNos);

    void save(CourseItem courseItem);
}
