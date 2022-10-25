package com.heartape;

import com.heartape.entity.Person;
import com.heartape.repository.PersonDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@SpringBootTest
public class ElasticsearchApplicationTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    private PersonDao personDao;

    /**
     * 保存文档
     * <p>目前spring data不适配elasticsearch 8,返回响应格式无法解析，但可以正常保存
     * <p>两种save方式均可，但个人更喜欢dao的方式
     */
    @Test
    public void save() {
        Person person = new Person(1L, "jackson", 35);
        personDao.save(person);
        // elasticsearchTemplate.save(person);
    }

}
