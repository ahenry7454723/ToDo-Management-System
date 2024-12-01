package com.dmm.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dmm.task.data.entity.Users;
import com.dmm.task.data.repository.UsersRepository;

@Service
public class UserService {

	@Autowired
	private UsersRepository usersRepository;

	public Users getUserByUserName(String userName) {
		return usersRepository.findByUserName(userName);
	}
}
