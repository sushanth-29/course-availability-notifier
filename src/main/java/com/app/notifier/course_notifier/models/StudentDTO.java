package com.app.notifier.course_notifier.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
	private String studentId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String emailId;
	private String username;
	private String password;
}
