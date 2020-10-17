package com.clear.zero.domain.model.product;

import lombok.*;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductNumber implements Serializable {
    private String value;

    public static ProductNumber of(Integer categoryId) {
        checkArgument(categoryId != null, "商品类目不能为空");
        checkArgument(categoryId > 0, "商品类目id不能小于0");
        return new ProductNumber(generateProductNo(categoryId));
    }

    public static ProductNumber of(String value) {
        checkArgument(!StringUtils.isEmpty(value), "商品编码不能为空");
        return new ProductNumber(value);
    }

    private static String generateProductNo(Integer categoryId) {
        String prefix = "PRODUCT";
        String typeStr = String.format("%04d", categoryId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currentTime = sdf.format(new Date());
        int randomNum = (int) (Math.random() * 9999 + 1);
        String randomNumStr = String.format("%04d", randomNum);
        return prefix + typeStr + currentTime + randomNumStr;
    }
}
