package com.app.notifier.course_notifier.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.notifier.course_notifier.models.StudentDTO;
import com.app.notifier.course_notifier.repositories.StudentRepo;
@RestController
@RequestMapping("/student")
public class StudentController{
	@Autowired
	StudentRepo studentRepo;
	@PutMapping("/update")
	public ResponseEntity<String> upsert(@RequestBody StudentDTO student){
		try {
			studentRepo.insertStudent(student);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("something went wrong..");
		}
		return ResponseEntity.ok().body("success");
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String studentId){
		try {
			studentRepo.delete(studentId);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Something went wrong..");
		}
		return ResponseEntity.ok().body("success");
	}
	@GetMapping("/get/{id}")
	public ResponseEntity<StudentDTO> get(@PathVariable("id") String studentId){
		try {
			return ResponseEntity.ok().body(studentRepo.getStudentById(studentId));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
