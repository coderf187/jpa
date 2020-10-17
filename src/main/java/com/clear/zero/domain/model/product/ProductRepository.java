package com.clear.zero.domain.model.product;

public interface ProductRepository {
    void save(Product product);

    void update(Product product);

    Product findByProductNo(ProductNumber productNo);
}
