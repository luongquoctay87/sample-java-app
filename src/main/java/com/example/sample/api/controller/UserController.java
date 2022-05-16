package com.example.sample.api.controller;


import com.example.sample.api.form.UserForm;
import com.example.sample.api.response.ApiResponse;
import com.example.sample.api.response.ErrorResponse;
import com.example.sample.api.response.ViewResponse;
import com.example.sample.enitity.AppUser;
import com.example.sample.model.UserDTO;
import com.example.sample.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping
    public ApiResponse getUserList(
            @RequestParam(name = "search", defaultValue = "") Optional<String> _search,
            @RequestParam(name = "pageNo", defaultValue = "1") Optional<Integer> _pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") Optional<Integer> _pageSize) {
        LOG.info("Request api GET api/v1/users");

        List<UserDTO> users = userService.findAll(_search.get(), _pageNo.get(), _pageSize.get())
                .stream().map(user -> mapper.map(user, UserDTO.class)).collect(Collectors.toList());

        long count = userService.count();
        ViewResponse view = ViewResponse.builder()
                .pageNo(_pageNo.get())
                .pageSize(_pageSize.get())
                .total(count)
                .items(users)
                .build();

        return new ApiResponse(HttpStatus.OK.value(), "users", view);
    }

    @GetMapping("/{id}")
    public ApiResponse getUser(@PathVariable("id") Long _id) {
        LOG.info(String.format("Request api GET api/v1/users/%s", _id));

        AppUser user = userService.getById(_id);

        return new ApiResponse(HttpStatus.OK.value(), "user", user);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    public ApiResponse createUser(@RequestBody UserForm form) {
        LOG.info("Request api POST api/v1/users");

        if (!form.getPassword().equals(form.getRePassword())) {
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "Password not match");
        }
        AppUser user = mapper.map(form, AppUser.class);
        user.setPassword(encoder.encode(user.getPassword()));

        try {
            AppUser newUser = userService.save(user);
            return new ApiResponse(HttpStatus.OK.value(), "Create user successful", newUser.getId());
        } catch (Exception e){
            LOG.info("Can not create user");
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "Create user fail");
        }
    }

    @PutMapping("/{id}")
    public ApiResponse updateUser(@RequestBody UserForm form, @PathVariable("id") Long _id) {
        LOG.info(String.format("Request api PUT api/v1/users/%s", _id));

        AppUser user = userService.getById(_id);
        if (user == null) {
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "User not found");
        }

        user.setUsername(form.getUsername());

        try {
            AppUser newUser = userService.save(user);
            return new ApiResponse(HttpStatus.OK.value(), "Update user successful", newUser.getId());
        } catch (Exception e){
            LOG.info(String.format("Can not update userId =%s", _id));
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "Update user fail");
        }
    }

    @PatchMapping("/changePassword/{id}")
    public ApiResponse changPassword(@RequestBody UserForm form, @PathVariable("id") Long _id) {
        LOG.info(String.format("Request api PATCH api/v1/users/changePassword/%s", _id));

        AppUser user = userService.getById(_id);
        if (user == null) {
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "User not found");
        }
        if (!form.getPassword().equals(form.getRePassword())) {
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "Password not match");
        }

        user.setPassword(encoder.encode(form.getPassword()));
        try {
            AppUser newUser = userService.save(user);
            return new ApiResponse(HttpStatus.OK.value(), "Change password successful", _id);
        } catch (Exception e){
            LOG.info(String.format("Can not change password, userId =%s", _id));
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "Change password fail");
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(@PathVariable(value = "id") Long _id) {
        LOG.info(String.format("Request api DELETE api/v1/users/%s", _id));

        try {
            userService.delete(_id);
            return new ApiResponse(HttpStatus.OK.value(), "Delete user successful");
        } catch (Exception e){
            LOG.info(String.format("Can not delete user, userId =%s", _id));
            return new ErrorResponse(HttpStatus.NO_CONTENT.value(), "Delete user fail");
        }
    }

}
