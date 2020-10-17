package com.clear.zero.domain.model.product;

import com.clear.zero.domain.exception.InvalidParameterException;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProductStatusEnum {
    // 新建
    DRAFTED(1000111, "草稿"),
    // 待审核
    AUDIT_PENDING(1000112, "待审核"),
    // 审核拒绝
    REJECTED(1000113, "审核拒绝"),
    // 审核通过
    APPROVED(1000114, "审核通过"),
    // 已上架
    LISTED(1000115, "已上架"),
    // 已下架
    UNLISTED(1000116, "已下架"),
    // 已失效
    EXPIRED(1000117, "已失效");

    @Getter
    @JsonValue
    private Integer code;

    @Getter
    private String remark;

    public static ProductStatusEnum of(Integer code) {
        ProductStatusEnum[] values = ProductStatusEnum.values();
        for (ProductStatusEnum val : values) {
            if (val.getCode().equals(code)) {
                return val;
            }
        }
        throw new InvalidParameterException(String.format("【%s】无效的产品状态", code));
    }

}
