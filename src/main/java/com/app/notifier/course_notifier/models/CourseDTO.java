package com.app.notifier.course_notifier.models;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseDTO{
	private String courseId;
	private String courseName;
	private Integer waitlistThresold;
	private Integer seats;
	private String  taughtBy;
	private String emailId;
	private LocalDate lastDay;
}
