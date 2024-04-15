package com.xuyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuan.entity.User;
import com.xuyuan.mapper.UserMapper;
import com.xuyuan.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
