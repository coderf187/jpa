package com.clear.zero.domain.model.product;

import com.clear.zero.application.command.CreateProductCommand;
import com.clear.zero.application.command.UpdateProductCommand;
import com.clear.zero.domain.common.model.AuditEntity;
import com.clear.zero.domain.common.model.Price;
import com.clear.zero.domain.exception.NotAllowedException;
import com.clear.zero.domain.exception.NotSupportedCurrencyException;
import com.google.common.base.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 商品聚合根
 * 没必要把所有的属性一股脑儿暴露出来，要修改“我”的属性，请调用“我”的方法，因为“我”的属性我自己最清楚
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@GenericGenerator(name = "CustomID", strategy = "com.clear.zero.util.CustomIDGenerator")
public class Product extends AuditEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "CustomID")
    private Long id;
    @Convert(converter = ProductNumberConverter.class)
    @Column(name = "product_no", length = 32, nullable = false, unique = true)
    private ProductNumber productNo;
    @Column(name = "name", length = 64, nullable = false)
    private String name;
    @Embedded
    private Price price;
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Convert(converter = ProductStatusConverter.class)
    @Column(name = "product_status", nullable = false)
    private ProductStatusEnum productStatus;
    @Column(name = "remark", length = 256)
    private String remark;

    @Column(name = "allow_across_category", nullable = false)
    private Boolean allowAcrossCategory;

    @ElementCollection(targetClass = ProductCourseItem.class)
    @CollectionTable(
            name = "product_course_item",
            uniqueConstraints = @UniqueConstraint(columnNames = {"product_no", "course_item_no"}),
            joinColumns = {@JoinColumn(name = "product_no", referencedColumnName = "product_no")}

    )
    private Set<ProductCourseItem> productCourseItems = new HashSet<>();


    public static Product of(CreateProductCommand command) {

        Integer categoryId = command.getCategoryId();
        checkArgument(!StringUtils.isEmpty(command.getName()), "商品名称不能为空");
        checkArgument(categoryId != null, "商品类目不能为空");
        checkArgument(categoryId > 0, "商品类目id不能小于0");
        // 生成产品码时有限制，该字段不能超过4位
        checkArgument(categoryId < 10000, "商品类目id不能超过10000");
        checkArgument(command.getAllowAcrossCategory() != null, "是否跨类目不能为空");

        Price price = Price.of(command.getCurrencyCode(), command.getPrice());
        if ("CAD".equalsIgnoreCase(price.getCurrency().getCurrencyCode())) {
            throw new NotSupportedCurrencyException(String.format("【%s】对不起，暂不支持该币种", command.getCurrencyCode()));
        }
        ProductNumber newProductNo = ProductNumber.of(categoryId);
        ProductStatusEnum defaultProductStatus = ProductStatusEnum.DRAFTED;

        Product product = new Product(null, newProductNo, command.getName(), price, categoryId, defaultProductStatus, command.getRemark(), command.getAllowAcrossCategory(), command.getProductCourseItems());
        product.setCreatedBy(1);
        product.setUpdatedBy(1);
        return product;
    }

    public void listing() {
        if (this.productStatus.getCode() < ProductStatusEnum.APPROVED.getCode()) {
            throw new NotAllowedException("已审核通过的商品才允许上架");
        }
        this.productStatus = ProductStatusEnum.LISTED;
    }

    public void unlisting() {
        if (!this.productStatus.equals(ProductStatusEnum.LISTED)) {
            throw new NotAllowedException("已上架的商品才允许下架");
        }
        this.productStatus = ProductStatusEnum.UNLISTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Product product = (Product) o;
        return Objects.equal(productNo, product.productNo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), productNo);
    }

    public void clearCourseItems() {
        this.productCourseItems = new HashSet<>();
    }

    public void mutate(UpdateProductCommand updateProductCommand) {
        this.name = updateProductCommand.getName();
        this.price = Price.of(updateProductCommand.getCurrencyCode(), updateProductCommand.getPrice());
        this.remark = updateProductCommand.getRemark();
        this.categoryId = updateProductCommand.getCategoryId();
        this.allowAcrossCategory = updateProductCommand.getAllowAcrossCategory();
        this.productCourseItems = updateProductCommand.getProductCourseItems();
    }
}
