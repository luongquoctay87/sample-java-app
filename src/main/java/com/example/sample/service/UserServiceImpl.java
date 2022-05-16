package com.example.sample.service;

import com.example.sample.config.ResourceNotFoundException;
import com.example.sample.enitity.AppUser;
import com.example.sample.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AppUser appUser = userRepo.findByUsername(username);
		if (appUser == null) {
			throw new UsernameNotFoundException("AppUser not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), getAuthority(username));
	}

	private List<SimpleGrantedAuthority> getAuthority(String username) {
		List<String> roles = userRepo.findRoleByUsername(username);
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (String role: roles) {
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
			authorities.add(authority);
		}

		return authorities;
	}

	@Override
	public List<AppUser> findAll(String keyword, int pageNo, int pageSize) {
		StringBuilder condition = new StringBuilder("%");
		condition.append(keyword);
		condition.append("%");

		if (pageNo > 0) pageNo = pageNo - 1;
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<AppUser> pagedResult = userRepo.findAllUsers(condition.toString(), pageable);
		if (pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<AppUser>();
		}
	}

	@Override
	public AppUser getById(long id) {
		Optional<AppUser> user = userRepo.findById(id);
		if (!user.isPresent()) {
			throw new ResourceNotFoundException("User not found", "userId", id);
		}
		return user.get();
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public void delete(long id) {
		userRepo.deleteById(id);
	}

	@Override
	public AppUser save(AppUser user) {
		return userRepo.save(user);
	}

}
