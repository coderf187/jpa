package com.clear.zero.application;

import com.clear.zero.application.command.CreateProductCommand;
import com.clear.zero.application.command.UpdateProductCommand;
import com.clear.zero.domain.model.product.Product;

public interface ProductService {
    Product createProduct(CreateProductCommand command);

    Integer unlistingProduct(String productNo);

    Product updateProduct(UpdateProductCommand updateProductCommand);
}
