package com.oo.tools.spring.boot.controller;

import com.oo.tools.spring.boot.entity.UserInfoEntity;
import com.oo.tools.spring.boot.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * UserInfoApi</p>
 *
 * @author Yu.ou
 * @desc:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoApi {
    
    private final UserInfoRepository userInfoRepository;
    
    @GetMapping("/list")
    public List<UserInfoEntity> list() {
        return userInfoRepository.selectList(null);
    }
    
    @GetMapping("/add")
    public String add() {
        UserInfoEntity entity = new UserInfoEntity();
        entity.setName("sadas");
        entity.setMobile(String.valueOf(new Random(90000000l).nextInt()));
        entity.setSex("1");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(1l);
        return userInfoRepository.insert(entity) > 0 ? "success" : "fail";
    }
}
