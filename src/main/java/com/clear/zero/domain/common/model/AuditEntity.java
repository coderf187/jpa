package com.clear.zero.domain.common.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class AuditEntity implements Serializable {
    @Column(name = "is_delete", columnDefinition = "TINYINT(1) DEFAULT 0")
    protected Boolean isDelete;
    @Column(name = "created_by", length = 11, nullable = false)
    protected Integer createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATETIME NULL DEFAULT CURRENT_TIMESTAMP")
    protected Date createdAt;
    @Column(name = "updated_by", length = 11, nullable = false)
    protected Integer updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    protected Date updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = createdAt = new Date();
        isDelete = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
