package com.clear.zero.application.impl;

import com.clear.zero.application.ProductService;
import com.clear.zero.application.command.CreateProductCommand;
import com.clear.zero.application.command.UpdateProductCommand;
import com.clear.zero.domain.exception.NotFoundException;
import com.clear.zero.domain.model.product.Product;
import com.clear.zero.domain.model.product.ProductCourseItem;
import com.clear.zero.domain.model.product.ProductManagement;
import com.clear.zero.domain.model.product.ProductNumber;
import com.clear.zero.domain.model.product.ProductRepository;
import com.clear.zero.domain.model.product.ProductStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class ProductSerivceImpl implements ProductService {

    private ProductRepository productRepository;
    private ProductManagement productManagement;

    public ProductSerivceImpl(ProductRepository productRepository, ProductManagement productManagement) {
        this.productRepository = productRepository;
        this.productManagement = productManagement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product createProduct(CreateProductCommand command) {
        Set<ProductCourseItem> productCourseItems = command.getProductCourseItems();
        if (CollectionUtils.isEmpty(productCourseItems)) {
            throw new IllegalArgumentException("明细不能为空");
        }

        // 不允许跨类目的商品，明细类目要跟商品类目保持一致。思来想去，这个逻辑还是放在domain service里好
        productManagement.checkCourseItemCategoryConsistence(command.getAllowAcrossCategory(), command.getCategoryId(), productCourseItems);
        Product product = Product.of(command);
        productRepository.save(product);
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer unlistingProduct(String productNo) {
        checkArgument(!StringUtils.isEmpty(productNo), "商品编号不能为空");
        Product product = productRepository.findByProductNo(ProductNumber.of(productNo));
        if (product == null) {
            throw new NotFoundException(String.format("商品【%s】未找到", productNo));
        }
        ProductStatusEnum oldStatus = product.getProductStatus();
        product.unlisting();
        productRepository.update(product);
        return oldStatus.getCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product updateProduct(UpdateProductCommand command) {
        String productNo = command.getProductNo();

        Product product = productRepository.findByProductNo(ProductNumber.of(productNo));
        if (product == null) {
            throw new NotFoundException(String.format("商品【%s】未找到", productNo));
        }

        Set<ProductCourseItem> productCourseItems = command.getProductCourseItems();
        if (CollectionUtils.isEmpty(productCourseItems)) {
            throw new IllegalArgumentException("明细不能为空");
        }

        // 不允许跨类目的商品，明细类目要跟商品类目保持一致。思来想去，这个逻辑还是放在domain service里好
        productManagement.checkCourseItemCategoryConsistence(command.getAllowAcrossCategory(), command.getCategoryId(), productCourseItems);
        product.mutate(command);
        productRepository.update(product);

        return null;
    }
}
