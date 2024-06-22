package com.app.notifier.course_notifier.repositories;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.app.notifier.course_notifier.constants.CourseConstants;
import com.app.notifier.course_notifier.constants.StudentConstants;
import com.app.notifier.course_notifier.models.StudentCourse;
import com.app.notifier.course_notifier.models.StudentDTO;
import com.app.notifier.course_notifier.models.StudentIdInfo;
import com.app.notifier.course_notifier.models.TokenDTO;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
@Repository
public class StudentRepo {
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	@Autowired
	private DynamoDbClient dynamoDbClient;
	public void insertStudent(StudentDTO studentDTO) {
		StudentCourse student = dynamoDBMapper.load(StudentCourse.class, StudentConstants.STUDENT_ID_PREFIX+studentDTO.getStudentId(),StudentConstants.STUDENT_ID_PREFIX+studentDTO.getStudentId());
		if(student==null) {
			//new student record
			String newStudentId = StudentConstants.STUDENT_ID_PREFIX+generateId();
			student = new StudentCourse();
			student.setPK(newStudentId);
			student.setSK(newStudentId);
		}
		student.setFirstName(studentDTO.getFirstName());
		student.setLastName(studentDTO.getLastName());
		student.setUsername(studentDTO.getUsername());
		student.setPassword(studentDTO.getPassword());
		student.setPhoneNumber(studentDTO.getPhoneNumber());
		student.setEmailId(studentDTO.getEmailId());
		dynamoDBMapper.save(student);
	}
	public void delete(String studenId){
		String partitionKey = StudentConstants.STUDENT_ID_PREFIX+studenId;
		HashMap<String,AttributeValue> eav = new HashMap<>();
		eav.put(":v1", new AttributeValue().withS(partitionKey));
		DynamoDBQueryExpression<StudentCourse> queryExpression = new DynamoDBQueryExpression<StudentCourse>()
				.withKeyConditionExpression("PK = :v1")
				.withExpressionAttributeValues(eav);
		List<StudentCourse> ddbResults = dynamoDBMapper.query(StudentCourse.class, queryExpression);
		for(StudentCourse studentCourse:ddbResults) {
			dynamoDBMapper.batchDelete(studentCourse);
		}
	}
	public StudentDTO getStudentById(String Id){
		String partionKey = StudentConstants.STUDENT_ID_PREFIX+Id;
		StudentCourse student = dynamoDBMapper.load(StudentCourse.class, partionKey, partionKey);
		String PK = student.getPK().substring(StudentConstants.STUDENT_ID_INDEX);
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.setStudentId(PK);
		studentDTO.setFirstName(student.getFirstName());
		studentDTO.setLastName(student.getLastName());
		studentDTO.setEmailId(student.getEmailId());
		studentDTO.setPhoneNumber(student.getPhoneNumber());
		return studentDTO;
	}
	public Integer generateId(){
		StudentIdInfo info = dynamoDBMapper.load(StudentIdInfo.class, StudentConstants.STUDENT_ID_GENERATOR_PK);
		int id = info.getValue();
		info.setValue(id+1);
		dynamoDBMapper.save(info);
		return id;
	}
	public TokenDTO getToken(String username,String password) {
		Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> eav = new HashMap<>();
		eav.put(":username", software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS(username));
		eav.put(":password", software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS(password));
		QueryRequest query = QueryRequest.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.indexName(StudentConstants.USERNAME_PASS_INDEX)
				.keyConditionExpression("username=:username and password=:password")
				.expressionAttributeValues(eav)
				.build();
		QueryResponse response = dynamoDbClient.query(query);
		if(response.hasItems()) {
			TokenDTO token = new TokenDTO();
			List<Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue>> items = response.items();
			for(Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> item: items) {
				token.setId(item.get("PK").s());
				token.setName(item.get("first_name").s()+" "+item.get("last_name").s());
			}
			return token;
		}else {
			return null;
		}
	}
}
