package com.heartape.repository;

import com.heartape.entity.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ElasticsearchRepository<实体类, 主键类型>
 */
@Repository
public interface PersonDao extends ElasticsearchRepository<Person, Long> {
}
