package com.heartape.service;

import com.heartape.entity.Phone;
import com.heartape.entity.User;

public interface IUserService {
    User getOne(Integer id);

    Phone getPhone(Integer id);
}
