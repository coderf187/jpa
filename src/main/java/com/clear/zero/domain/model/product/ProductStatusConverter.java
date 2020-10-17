package com.clear.zero.domain.model.product;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProductStatusConverter implements AttributeConverter<ProductStatusEnum, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductStatusEnum statusEnum) {
        return statusEnum.getCode();
    }

    @Override
    public ProductStatusEnum convertToEntityAttribute(Integer statusCode) {
        return ProductStatusEnum.of(statusCode);
    }
}
