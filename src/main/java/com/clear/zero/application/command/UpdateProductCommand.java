package com.clear.zero.application.command;

import com.clear.zero.domain.model.product.ProductCourseItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductCommand {
    private String productNo;
    private String name;
    private Integer categoryId;
    private String currencyCode;
    private BigDecimal price;
    private String remark;
    private Boolean allowAcrossCategory;
    private Set<ProductCourseItem> productCourseItems;

    public static UpdateProductCommand of(String productNo, String name, Integer categoryId, String currencyCode, BigDecimal price, String remark, Boolean allowAcrossCategory, Set<ProductCourseItem> productCourseItems) {
        checkArgument(!StringUtils.isEmpty(productNo), "商品编号不能为空");
        // 检查是否有重复的明细编码，考虑再三，还是要校验一下，不然突然少了一个明细，会吓到用户。
        Set<String> dup = getDuplicatedItemNos(productCourseItems);
        if (!CollectionUtils.isEmpty(dup)) {
            throw new IllegalArgumentException(String.format("明细编号不能重复【%s】", String.join(",", dup)));
        }
        return new UpdateProductCommand(productNo, name, categoryId, currencyCode, price, remark, allowAcrossCategory, productCourseItems);
    }

    private static Set<String> getDuplicatedItemNos(Set<ProductCourseItem> productCourseItems) {
        if (CollectionUtils.isEmpty(productCourseItems)) {
            return null;
        }
        Map<String, Long> duplicatedNoMap = productCourseItems.stream().collect(collectingAndThen(groupingBy(ProductCourseItem::getCourseItemNo, counting()),
                m -> {
                    m.values().removeIf(v -> v <= 1);
                    return m;
                }));
        return duplicatedNoMap.keySet();
    }
}
