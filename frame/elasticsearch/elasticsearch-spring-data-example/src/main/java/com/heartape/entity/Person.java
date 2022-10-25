package com.heartape.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@Document(indexName = "person")
public class Person implements Serializable {

    @Serial
    private static final long serialVersionUID = 738861599311088983L;

    @Id
    private Long id;

    // @Field(type = FieldType.Text, analyzer = "ik_max_word")
    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

}
