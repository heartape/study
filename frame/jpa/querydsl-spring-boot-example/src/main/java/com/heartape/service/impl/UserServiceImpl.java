package com.heartape.service.impl;

import com.heartape.entity.Phone;
import com.heartape.entity.QPhone;
import com.heartape.entity.QUser;
import com.heartape.entity.User;
import com.heartape.service.IUserService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private JPAQueryFactory queryFactory;

    /**
     * 单表查询
     * <li>使用QUser和QPhone类之前，需要先进行compile
     * <li>然后根据maven apt-maven-plugin插件的outputDirectory,将target/generated-sources/java文件夹“mark directory as -> generated sources root”
     * <li>最后直接在项目中使用QUser和QPhone类，而不用将这两个类拷入项目
     */
    @Override
    public User getOne(Integer id) {
        QUser user = QUser.user;
        return queryFactory
                .select(user)
                .from(user)
                .where(user.id.eq(id))
                .fetchOne();
    }

    /**
     * 连表查询
     */
    @Override
    public Phone getPhone(Integer id) {
        QUser user = QUser.user;
        QPhone phone = QPhone.phone;
        return queryFactory
                .select(phone)
                .from(user)
                .leftJoin(phone)
                .on(phone.id.eq(user.phoneId))
                .where(user.id.eq(id))
                .fetchOne();
    }
}
