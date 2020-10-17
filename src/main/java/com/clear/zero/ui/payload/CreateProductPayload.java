package com.clear.zero.ui.payload;

import com.clear.zero.application.command.CreateProductCommand;
import com.clear.zero.domain.model.product.ProductCourseItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CreateProductPayload {
    private String name;
    private Integer categoryId;
    private String currencyCode;
    private BigDecimal price;
    private String remark;
    private Boolean allowAcrossCategory;
    private Set<ProductCourseItemPayload> productCourseItems;

    public CreateProductCommand toCommand() {
        Set<ProductCourseItem> itemRelations = productCourseItems.stream()
                .map(item -> ProductCourseItem.of(item.getCourseItemNo(), item.getNewPrice())).collect(Collectors.toSet());
        return CreateProductCommand.of(name, categoryId, currencyCode, price, remark, allowAcrossCategory, itemRelations);
    }

    @Data
    public static class ProductCourseItemPayload {
        private String courseItemNo;
        private BigDecimal newPrice;

    }
}
