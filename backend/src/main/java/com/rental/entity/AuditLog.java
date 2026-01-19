package com.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log", schema = "rental_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Integer auditId;
    
    @Column(name = "table_name", length = 50)
    private String tableName;
    
    @Column(name = "record_id")
    private Integer recordId;
    
    @Column(name = "action", length = 10)
    private String action; // INSERT, UPDATE, DELETE
    
    @Column(name = "old_data", columnDefinition = "JSONB")
    private String oldData;
    
    @Column(name = "new_data", columnDefinition = "JSONB")
    private String newData;
    
    @Column(name = "changed_by", length = 100)
    private String changedBy;
    
    @CreatedDate
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}


