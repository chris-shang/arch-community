package com.cshang.arch.community.client.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.cshang.arch.community.user.model.LoginRequest;
import com.cshang.arch.community.user.model.User;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(serviceId = "communityService", url = "${arch.community.url}/user")
public interface UserService {

    @GetMapping(path = "/name")
    CompletableFuture<Optional<User>> findUserByUserName(
            @RequestParam("userName")
            String userName);

    @GetMapping(path = "/id")
    CompletableFuture<Optional<User>> findUserById(
            @RequestParam("id")
            Long userId);

    @PostMapping
    CompletableFuture<User> saveUser(
            @RequestBody
            LoginRequest loginRequest);

    @PutMapping
    CompletableFuture<User> updateUser(
            @RequestBody
            LoginRequest loginRequest);

    @DeleteMapping(value = "/id")
    CompletableFuture<Optional<User>> deleteUser(
            @RequestParam("userId")
            Long userId);

}
