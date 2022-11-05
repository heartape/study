package com.heartape.service.impl;

import com.heartape.entity.Phone;
import com.heartape.entity.QPhone;
import com.heartape.entity.QUser;
import com.heartape.entity.User;
import com.heartape.repository.UserRepository;
import com.heartape.service.IUserService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private UserRepository userRepository;

    /**
     * 单表查询，必要时与hibernate结合使用
     * <li>使用QUser和QPhone类之前，需要先进行compile
     * <li>然后根据maven apt-maven-plugin插件的outputDirectory,将target/generated-sources/java文件夹“mark directory as -> generated sources root”
     * <li>最后直接在项目中使用QUser和QPhone类，而不用将这两个类拷入项目
     */
    @Override
    public User getOne(Integer id) {
        // hibernate crud接口
        User user1 = userRepository.findById(id).orElseThrow(RuntimeException::new);
        Page<User> userPage = userRepository.findAll(PageRequest.of(1, 10));

        QUser user = QUser.user;
        // Querydsl crud接口
        User user2 = userRepository.findOne(user.id.eq(id)).orElseThrow(RuntimeException::new);
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
