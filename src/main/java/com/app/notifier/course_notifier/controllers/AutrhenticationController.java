package com.app.notifier.course_notifier.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.notifier.course_notifier.models.TokenDTO;
import com.app.notifier.course_notifier.repositories.StudentRepo;
@RestController
public class AutrhenticationController {
	@Autowired
	StudentRepo studentRepo;
	@GetMapping("/login")
	@CrossOrigin(origins = "http://localhost:4200")
	public TokenDTO authenticateUser(@RequestParam("username") String username, @RequestParam("password") String password) {
		try {
			return studentRepo.getToken(username, password);
		} catch (Exception e) {
			System.out.println("Internal Server Error");
		}
		return null;
	}
}
