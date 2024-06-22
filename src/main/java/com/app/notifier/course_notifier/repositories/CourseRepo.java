package com.app.notifier.course_notifier.repositories;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.app.notifier.course_notifier.constants.CourseConstants;
import com.app.notifier.course_notifier.constants.StudentConstants;
import com.app.notifier.course_notifier.models.CourseDTO;
import com.app.notifier.course_notifier.models.CourseIDInfo;
import com.app.notifier.course_notifier.models.StudentCourse;
import com.app.notifier.course_notifier.models.StudentCourseDTO;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
@Repository
public class CourseRepo {
	@Autowired
	DynamoDBMapper dynamoDBMapper;
	@Autowired
	DynamoDbClient dynamoDbClient;
	public void updateCourse(CourseDTO courseDTO){
		StudentCourse course = dynamoDBMapper.load(StudentCourse.class,CourseConstants.COURSE_ID_PREFIX+courseDTO.getCourseId(),CourseConstants.COURSE_ID_PREFIX+courseDTO.getCourseId());
		if(course==null) {
			String newCourseId = CourseConstants.COURSE_ID_PREFIX+generateCourseId();
			course = new StudentCourse();
			course.setPK(newCourseId);
			course.setSK(newCourseId);
		}
		course.setCourseName(courseDTO.getCourseName());
		course.setSeats(courseDTO.getSeats());
		course.setWaitlistThreshold(courseDTO.getWaitlistThresold());
		course.setTaughtBy(courseDTO.getTaughtBy());
		course.setEmailId(courseDTO.getEmailId());
		course.setRegistrationEndDate(dateToEpoch(courseDTO.getLastDay()));
		dynamoDBMapper.save(course);
	}
	public List<CourseDTO> getCourses(){
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(CourseConstants.COURSE_ID_PREFIX));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withIndexName("course_student")
				.withFilterExpression("begins_with(SK,:val1) and begins_with(PK,:val1)")
				.withExpressionAttributeValues(eav);
		List<StudentCourse> courses =  dynamoDBMapper.scan(StudentCourse.class, scanExpression);
		List<CourseDTO> coursesDTO = new ArrayList<>();
		for(StudentCourse course : courses) {
			CourseDTO courseDTO = new CourseDTO();
			courseDTO.setCourseId(course.getPK().substring(CourseConstants.COURSE_START_ID_INDEX));
			courseDTO.setCourseName(course.getCourseName());
			courseDTO.setTaughtBy(course.getTaughtBy());
			courseDTO.setSeats(course.getSeats());
			courseDTO.setWaitlistThresold(course.getWaitlistThreshold());
			courseDTO.setEmailId(course.getEmailId());
			courseDTO.setLastDay(epochToDate(course.getRegistrationEndDate()));
			coursesDTO.add(courseDTO);
		}
		return coursesDTO;
	}
	public List<StudentCourseDTO> getCoursesbyStudentID(String Id){
		Id = StudentConstants.STUDENT_ID_PREFIX+Id;
		Map<String,software.amazon.awssdk.services.dynamodb.model.AttributeValue> eav = new HashMap<>();
		eav.put(":pk", software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS(Id));
		eav.put(":coursePrefix", software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS(CourseConstants.COURSE_ID_PREFIX));
		QueryRequest query = QueryRequest.builder()
				.keyConditionExpression("PK=:pk and begins_with(SK,:coursePrefix)")
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.expressionAttributeValues(eav)
				.build();
		List<Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue>> courses = dynamoDbClient.query(query).items();
		List<StudentCourseDTO> studentCourses = new ArrayList<>();
		for(Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> course : courses) {
			StudentCourseDTO studentCourse = new StudentCourseDTO();
			studentCourse.setCourseId(course.get("SK").s().substring(7));
			studentCourse.setEnrollMentDate(getDate(course.get("enrollmentDate").s()));
			studentCourse.setStatus(course.get("status").s());
			studentCourses.add(studentCourse);
		}
		return studentCourses;
	}
	private String getDate(String epoch) {
		if(epoch==null || epoch.isEmpty()) return "";
		Date date = new Date(Long.valueOf(epoch));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	private String generateCourseId(){
		CourseIDInfo courseIDInfo = dynamoDBMapper.load(CourseIDInfo.class,CourseConstants.COURSE_ID_GENERATOR_PK);
		Integer currentIDSequence = courseIDInfo.getValue();
		courseIDInfo.setValue(currentIDSequence+1);
		dynamoDBMapper.save(courseIDInfo);
		return String.valueOf(currentIDSequence);
	}
	private Long dateToEpoch(LocalDate localDate){
		if(localDate==null) {
			return null;
		}
		return localDate.toEpochDay();
	}
	private LocalDate epochToDate(Long epoch) {
		if(epoch==null) {
			return null;
		}
		return LocalDate.ofEpochDay(epoch);
	}
}
