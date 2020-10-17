package com.clear.zero.ui.api;

import com.clear.zero.application.CourseItemService;
import com.clear.zero.application.command.CreateCourseItemCommand;
import com.clear.zero.domain.model.courseitem.CourseItem;
import com.clear.zero.ui.payload.CreateCourseItemPayload;
import com.clear.zero.ui.results.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseItemController {
    private CourseItemService courseItemService;

    public CourseItemController(CourseItemService courseItemService) {
        this.courseItemService = courseItemService;
    }

    @PostMapping("/api/v1/course-item/create")
    public ApiResult<CourseItem> createCourseItem(@RequestBody CreateCourseItemPayload createCourseItemPayload) {
        CreateCourseItemCommand command = createCourseItemPayload.toCommand();
        CourseItem courseItem = courseItemService.createCourseItem(command);
        return ApiResult.ok(courseItem);
    }

//    @PostMapping("/api/v1/course-item/move")
//    public ApiResult<Integer> addItems(@RequestBody UnlistingProductPayload unlistingProductPayload){
//        Integer oldStatus = productService.unlistingProduct(unlistingProductPayload.getProductNo());
//        return ApiResult.ok(oldStatus);
//    }
}
