package com.app.notifier.course_notifier.controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.notifier.course_notifier.models.CourseDTO;
import com.app.notifier.course_notifier.models.StudentCourseDTO;
import com.app.notifier.course_notifier.repositories.CourseRegistartionRepo;
import com.app.notifier.course_notifier.repositories.CourseRepo;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {
	@Autowired
	CourseRepo courseRepo;
	@Autowired
	CourseRegistartionRepo courseRegistartionRepo;
	@PutMapping("/upsert")
	public ResponseEntity<String> upsert(@RequestBody CourseDTO courseDTO){
		try {
			courseRepo.updateCourse(courseDTO);
		} catch (Exception e) {
			
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
		return  ResponseEntity.ok().body("success");
	}
	@GetMapping("/courses")
	public ResponseEntity<List<CourseDTO>> allCourses(){
		try {
			List<CourseDTO> course = courseRepo.getCourses();
			return ResponseEntity.ok().body(course);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.internalServerError().body(new ArrayList<>());
		}
	}
	@PutMapping("/enroll/{studentId}")
	public ResponseEntity<String> enrollCourses(@PathVariable("studentId") String studentId, @RequestBody List<CourseDTO> courses){
		try {
			courseRegistartionRepo.enroll(studentId,courses);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
	@DeleteMapping("/drop/{studentID}/{courseID}")
	public ResponseEntity<String> dropCourses(@PathVariable("studentID") String studentID, @PathVariable("courseID") String courseID){
		try {
			courseRegistartionRepo.drop(studentID,courseID);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
	@GetMapping("/courses/{studentId}")
	public List<StudentCourseDTO> getCoursesByStudentID(@PathVariable("studentId") String studentId){
		try {
			return courseRepo.getCoursesbyStudentID(studentId);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
