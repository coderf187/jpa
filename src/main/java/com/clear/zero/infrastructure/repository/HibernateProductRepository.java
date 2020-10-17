package com.clear.zero.infrastructure.repository;

import com.clear.zero.domain.model.product.Product;
import com.clear.zero.domain.model.product.ProductNumber;
import com.clear.zero.domain.model.product.ProductRepository;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

@Repository
public class HibernateProductRepository extends HibernateSupport<Product> implements ProductRepository {
    HibernateProductRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Product findByProductNo(ProductNumber productNo) {
        if (StringUtils.isEmpty(productNo)) {
            return null;
        }
        Query<Product> query = getSession().createQuery("from Product where productNo=:productNo and isDelete=0", Product.class).setParameter("productNo", productNo);
        return query.uniqueResult();
    }
}
