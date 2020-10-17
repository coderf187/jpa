package com.clear.zero.ui.api;

import com.clear.zero.application.ProductService;
import com.clear.zero.application.command.CreateProductCommand;
import com.clear.zero.application.command.UpdateProductCommand;
import com.clear.zero.domain.model.product.Product;
import com.clear.zero.ui.payload.CreateProductPayload;
import com.clear.zero.ui.payload.UnlistingProductPayload;
import com.clear.zero.ui.payload.UpdateProductPayload;
import com.clear.zero.ui.results.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/api/v1/product/create")
    public ApiResult<Product> createProduct(@RequestBody CreateProductPayload createProductPayload) {
        CreateProductCommand command = createProductPayload.toCommand();
        Product product = productService.createProduct(command);
        return ApiResult.ok(product);
    }

    @PostMapping("/api/v1/product/unlisting")
    public ApiResult<Integer> unlistingProduct(@RequestBody UnlistingProductPayload unlistingProductPayload) {
        Integer oldStatus = productService.unlistingProduct(unlistingProductPayload.getProductNo());
        return ApiResult.ok(oldStatus);
    }

    @PostMapping("/api/v1/product/update")
    public ApiResult<Product> addItems(@RequestBody UpdateProductPayload updateProductPayload) {
        UpdateProductCommand updateProductCommand = updateProductPayload.toCommand();
        Product product = productService.updateProduct(updateProductCommand);
        return ApiResult.ok(product);
    }
}
