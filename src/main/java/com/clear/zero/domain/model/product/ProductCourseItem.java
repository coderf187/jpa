package com.clear.zero.domain.model.product;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 商品-课程明细关系
 * 注意ProductCourseItem被设计成值对象，它有以下特点
 * 1.值对象是只读的, 所有属性不提供setter
 * 2.只有属性值相同就认为对象是相同的。所以必须要重写hashCode和equals方法
 * 3.如果要更新某个字段，只能new一个新的值对象
 * 4.jpa要求必须得有一个无参构造函数，把它定为proctected
 * 5.构造函数定义成私有，只能通过工厂方法创建
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCourseItem implements Serializable {
    @Column(name = "course_item_no", length = 32, nullable = false)
    private String courseItemNo;
    @Column(name = "new_price", precision = 10, scale = 2)
    private BigDecimal newPrice;

    public static ProductCourseItem of(String courseItemNo, BigDecimal newPrice) {
        checkArgument(courseItemNo != null, "课程明细编号不能为空");
        return new ProductCourseItem(courseItemNo, newPrice);
    }

}
