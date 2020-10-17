package com.clear.zero.domain.model.product;

import com.clear.zero.domain.exception.CategoryNotMatchException;
import com.clear.zero.domain.exception.NotFoundException;
import com.clear.zero.domain.model.courseitem.CourseItem;
import com.clear.zero.domain.model.courseitem.CourseItemNumber;
import com.clear.zero.domain.model.courseitem.CourseItemRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.groupingBy;

@Component
public class ProductManagement {
    private CourseItemRepository courseItemRepository;

    public ProductManagement(CourseItemRepository courseItemRepository) {
        this.courseItemRepository = courseItemRepository;
    }

    /**
     * 检查明细的项目跟商品的项目是否保持一致
     * 因为涉及了另一个聚合根CourseItem，把CourseItem实体转成值对象好麻烦
     * 所以把这段逻辑放在domain service里
     *
     * @param allowCrossCategory 是否允许跨类目
     * @param categoryId         商品类目id
     * @param productCourseItems 明细信息
     */
    public void checkCourseItemCategoryConsistence(Boolean allowCrossCategory, Integer categoryId, Set<ProductCourseItem> productCourseItems) {
        checkArgument(allowCrossCategory != null, "是否允许跨类目不能为空");
        checkArgument(categoryId != null, "商品类目不能为空");

        // 检查编码对应的明细是否存在，这个算不算business logic
        List<CourseItemNumber> itemNos = productCourseItems.stream().map(item -> CourseItemNumber.of(item.getCourseItemNo())).collect(Collectors.toList());
        List<CourseItem> courseItems = courseItemRepository.findByItemNos(itemNos);
        Map<CourseItemNumber, List<CourseItem>> courseItemMap = courseItems.stream().collect(groupingBy(CourseItem::getItemNo));
        List<String> notFoundItemNos = itemNos.stream().filter(itemNo -> !courseItemMap.containsKey(itemNo))
                .map(item -> item.getValue())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(notFoundItemNos)) {
            throw new NotFoundException(String.format("明细【%s】未找到", String.join(",", notFoundItemNos)));
        }

        // 不允许跨类目时才需要检查
        if (!allowCrossCategory) {
            List<CourseItem> unmatchedCourseItems = getUnmatchedCourseItems(categoryId, courseItems);
            if (!CollectionUtils.isEmpty(unmatchedCourseItems)) {
                List<String> unmatchedItemNos = unmatchedCourseItems.stream().map(item -> item.getItemNo().getValue()).collect(Collectors.toList());
                throw new CategoryNotMatchException(String.format("明细【%s】类目不匹配", String.join(",", unmatchedItemNos)));
            }
        }
    }

    private List<CourseItem> getUnmatchedCourseItems(Integer productCategoryId, List<CourseItem> courseItems) {
        return courseItems.stream().filter(item -> !item.getCategoryId().equals(productCategoryId))
                .collect(Collectors.toList());
    }


}
