package com.heartape.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * user实体类
 * <li>columnList只能填写实体类字段，而不是数据库字段
 */
@Getter
@Setter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user", indexes = {
        @Index(name = "phone_id", columnList = "phoneId", unique = false)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "name")
    private String name;
    @Column(columnDefinition = "phone_id")
    private Integer phoneId;
}
