package com.example.sample.service;

import com.example.sample.enitity.AppUser;
import java.util.List;

public interface UserService {

    List<AppUser> findAll(String keyword, int pageNo, int pageNumber);

    AppUser getById(long id);

    long count();

    AppUser save(AppUser user);

    void delete(long id);
}
