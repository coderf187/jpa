package com.clear.zero.infrastructure.repository;

import com.clear.zero.domain.model.courseitem.CourseItem;
import com.clear.zero.domain.model.courseitem.CourseItemNumber;
import com.clear.zero.domain.model.courseitem.CourseItemRepository;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HibernateCourseItemRepository extends HibernateSupport<CourseItem> implements CourseItemRepository {
    HibernateCourseItemRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public CourseItem findByItemNo(CourseItemNumber itemNo) {
        if (StringUtils.isEmpty(itemNo)) {
            return null;
        }
        Query<CourseItem> query = getSession().createQuery("from CourseItem where itemNo=:itemNo and isDelete=0", CourseItem.class).setParameter("itemNo", itemNo);
        return query.uniqueResult();
    }

    @Override
    public List<CourseItem> findByItemNos(List<CourseItemNumber> itemNos) {
        if (CollectionUtils.isEmpty(itemNos)) {
            return new ArrayList<>();
        }
        Query<CourseItem> query = getSession().createQuery("from CourseItem where itemNo in (:itemNos) and isDelete=0", CourseItem.class).setParameterList("itemNos", itemNos);
        return query.getResultList();
    }
}
