package com.clear.zero.domain.model.product;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProductNumberConverter implements AttributeConverter<ProductNumber, String> {
    @Override
    public String convertToDatabaseColumn(ProductNumber productNumber) {
        return productNumber.getValue();
    }

    @Override
    public ProductNumber convertToEntityAttribute(String value) {
        return ProductNumber.of(value);
    }
}
