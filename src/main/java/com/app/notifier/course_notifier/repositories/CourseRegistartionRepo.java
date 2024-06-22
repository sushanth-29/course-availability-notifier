package com.app.notifier.course_notifier.repositories;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.app.notifier.course_notifier.constants.CourseConstants;
import com.app.notifier.course_notifier.constants.StudentConstants;
import com.app.notifier.course_notifier.models.CourseDTO;
import com.app.notifier.course_notifier.models.StudentCourse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.Delete;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.Update;
@Repository
public class CourseRegistartionRepo{
	@Autowired
	DynamoDBMapper dynamoDBMapper;
	@Autowired
	DynamoDbClient dynamoDbClient;
	public void enroll(String studentId,List<CourseDTO> courses){
		studentId = StudentConstants.STUDENT_ID_PREFIX+studentId;
		for(CourseDTO course : courses) {
			String courseId = CourseConstants.COURSE_ID_PREFIX+course.getCourseId();
			StudentCourse studentCourse = dynamoDBMapper.load(StudentCourse.class,studentId,courseId);
			if(isRegularSeatsAvailable(courseId)){	
				if(studentCourse==null){
					newEnrollment(studentId,courseId,CourseConstants.ENROLLED);
				}else if(studentCourse.getStatus().equals(CourseConstants.WAITLIST_STATUS)){
					updateCourseStatus(studentCourse);
				}
			}else if(isWaitListAvailable(courseId)) {
				if(studentCourse==null) {
					newEnrollment(studentId,courseId,CourseConstants.WAITLIST_STATUS);
				}else if(studentCourse.getStatus().equals(CourseConstants.INTERESTED_STATUS)) {
					updateCourseStatus(studentCourse);
				}
			}else{
				//interested students
				newEnrollment(studentId, courseId, CourseConstants.INTERESTED_STATUS);
			}
		}
	}
	public void drop(String studentId,String courseId) {
		studentId= StudentConstants.STUDENT_ID_PREFIX+studentId;
		courseId = CourseConstants.COURSE_ID_PREFIX+courseId;
		StudentCourse studentCourse = dynamoDBMapper.load(StudentCourse.class,studentId, courseId);
		if(studentCourse!=null && studentCourse.getStatus().equals(CourseConstants.ENROLLED)) {
			dropCourse(studentId, courseId, CourseConstants.ENROLLED);
		}else if(studentCourse!=null && studentCourse.getStatus().equals(CourseConstants.WAITLIST_STATUS)) {
			dropCourse(studentId, courseId, CourseConstants.WAITLIST_STATUS);
		}else if(studentCourse!=null && studentCourse.getStatus().equals(CourseConstants.INTERESTED_STATUS)){
			dropCourse(studentId,courseId,CourseConstants.INTERESTED_STATUS);
		}
	}
	private void dropCourse(String PK,String SK,String status){
		Map<String, AttributeValue> deleteKeys = new HashMap<>();
		deleteKeys.put("PK",AttributeValue.fromS(PK));
		deleteKeys.put("SK",AttributeValue.fromS(SK));
		Map<String, AttributeValue> updateKeys = new HashMap<>();
		updateKeys.put("PK",AttributeValue.fromS(SK));
		updateKeys.put("SK",AttributeValue.fromS(SK));
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", AttributeValue.fromN(String.valueOf(1)));
		Delete delete = Delete.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.key(deleteKeys)
				.build();
		Update update = Update.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.key(updateKeys)
				.updateExpression(status.equals(CourseConstants.ENROLLED)?"SET seats = seats + :val1":"SET waitlist_allowed = waitlist_allowed + :val1")
				.expressionAttributeValues(eav)
				.build();
		TransactWriteItem deleteTransaction = TransactWriteItem.builder()
				.delete(delete)
				.build();
		TransactWriteItem updateTransaction = TransactWriteItem.builder()
				.update(update)
				.build();
		List<TransactWriteItem> transactionList = new ArrayList<>();
		transactionList.add(deleteTransaction);
		if(status.equals(CourseConstants.ENROLLED)||status.equals(CourseConstants.WAITLIST_STATUS)) {
			transactionList.add(updateTransaction);
		}
		TransactWriteItemsRequest dropTransaction = TransactWriteItemsRequest.builder()
				.transactItems(transactionList)
				.build();
		dynamoDbClient.transactWriteItems(dropTransaction);
	}
	private boolean isRegularSeatsAvailable(String courseId) {
		StudentCourse course = dynamoDBMapper.load(StudentCourse.class,courseId,courseId,
				ConsistentReads.CONSISTENT.config());
		if(course!=null && course.getSeats()>0) {
			return true;
		}
		return false;
	}
	private boolean isWaitListAvailable(String courseId){
		StudentCourse course = dynamoDBMapper.load(StudentCourse.class,courseId,courseId,
				ConsistentReads.CONSISTENT.config());
		if(course!=null && course.getWaitlistThreshold()>0) {
			return true;
		}
		return false;
	}
	private void newEnrollment(String studentId,String courseId,String status){
		Map<String, AttributeValue> item = new HashMap<>();
		Map<String, AttributeValue> eav = new HashMap<>();
		Map<String, AttributeValue> key = new HashMap<>();
		item.put(CourseConstants.PK, AttributeValue.fromS(studentId));
		item.put(CourseConstants.SK, AttributeValue.fromS(courseId));
		item.put(CourseConstants.STATUS, AttributeValue.fromS(status));
		item.put(CourseConstants.ENROLLMENT_TIME, AttributeValue.fromN(String.valueOf(System.currentTimeMillis())));
		eav.put(":val1", AttributeValue.fromN(String.valueOf(1)));
		key.put("PK",AttributeValue.fromS(courseId));
		key.put("SK", AttributeValue.fromS(courseId));
		Put newCoursePut = Put.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.item(item)
				.build();
		Update updateSeatCount = Update.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.updateExpression(status.equals(CourseConstants.ENROLLED)?"SET seats = seats - :val1":"SET waitlist_allowed = waitlist_allowed - :val1")
				.key(key)
				.expressionAttributeValues(eav)
				.build();
		TransactWriteItem updateTransaction = TransactWriteItem.builder()
				.update(updateSeatCount)
				.build();
		TransactWriteItem putTransaction = TransactWriteItem.builder()
				.put(newCoursePut)
				.build();
		
		TransactWriteItemsRequest enrollTransaction = TransactWriteItemsRequest.builder()
				.transactItems(status.equals(CourseConstants.INTERESTED_STATUS)?Arrays.asList(putTransaction):Arrays.asList(putTransaction,updateTransaction))
				.build();
		dynamoDbClient.transactWriteItems(enrollTransaction);
	}
	private void updateCourseStatus(StudentCourse studentCourse) {
		//update status
		//decrement seat count
		//increment wait list count
		Map<String, AttributeValue> statusEav = new HashMap<>();
		statusEav.put(":courseStatus",AttributeValue.fromS(studentCourse.getStatus().equals(CourseConstants.WAITLIST_STATUS)?CourseConstants.ENROLLED:CourseConstants.WAITLIST_STATUS));
		statusEav.put(":enrollementTime",AttributeValue.fromN(String.valueOf(System.currentTimeMillis())));
		Map<String, String> statusEaN = new HashMap<>();
		statusEaN.put("#s","status");
		Map<String, AttributeValue> eav = new HashMap<>();
		eav.put(":val1", AttributeValue.fromN(String.valueOf(1)));
		Map<String, AttributeValue> courseKey = new HashMap<>();
		Map<String, AttributeValue> studentCourseKey = new HashMap<>();
		courseKey.put("PK",AttributeValue.fromS(studentCourse.getSK()));
		courseKey.put("SK", AttributeValue.fromS(studentCourse.getSK()));
		studentCourseKey.put("PK",AttributeValue.fromS(studentCourse.getPK()));
		studentCourseKey.put("SK", AttributeValue.fromS(studentCourse.getSK()));
		//student status update
		Update studentStatusUpdate = Update.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.key(studentCourseKey)
				.updateExpression("SET #s = :courseStatus,enrollmentDate=:enrollementTime")
				.expressionAttributeValues(statusEav)
				.expressionAttributeNames(statusEaN)
				.build();
		//course seat and wait list update
		Update seatAndWaitlistUpdate = Update.builder()
				.tableName(CourseConstants.STUDENT_COURSE_TABLE)
				.updateExpression(studentCourse.getStatus().equals(CourseConstants.WAITLIST_STATUS)?"SET waitlist_allowed = waitlist_allowed + :val1, seats = seats - :val1":"SET waitlist_allowed = waitlist_allowed - :val1")
				.expressionAttributeValues(eav)
				.key(courseKey)
				.build();
		//create transactions
        List<TransactWriteItem> transactionList = new ArrayList<>();
		TransactWriteItem seatAndWaitlistUpdateTransaction = TransactWriteItem.builder()
				.update(seatAndWaitlistUpdate)
				.build();
		TransactWriteItem studentStatusUpdateTransaction = TransactWriteItem.builder()
				.update(studentStatusUpdate)
				.build();
		transactionList.add(seatAndWaitlistUpdateTransaction);
		transactionList.add(studentStatusUpdateTransaction);
		if(studentCourse.getStatus().equals(CourseConstants.WAITLIST_STATUS)) {
			addActiveJobItemsToTransaction(transactionList, studentCourse.getPK(),studentCourse.getSK());
		}
		//execute transactions
		TransactWriteItemsRequest transactions = TransactWriteItemsRequest.builder()
				.transactItems(transactionList)
				.build();
		dynamoDbClient.transactWriteItems(transactions);
	}
	private void addActiveJobItemsToTransaction(List<TransactWriteItem> transactionList, String studentId, String courseId) {
		for(String key[] : getJobKeys(studentId, courseId)) {
			Map<String,AttributeValue> keys = new HashMap<>();
			keys.put("PK",AttributeValue.fromS(key[0]));
			keys.put("SK",AttributeValue.fromS(courseId));
			Map<String,AttributeValue> expAttrVals = new HashMap<>();
			expAttrVals.put(":deadline",AttributeValue.fromN(key[1]));
			expAttrVals.put(":notValid",AttributeValue.fromS("N"));
			Update updateJob = Update.builder()
					.tableName(CourseConstants.MONITOR_JOBS_TABLE)
					.key(keys)
					.conditionExpression("deadline=:deadline")
					.updateExpression("SET valid=:notValid")
					.expressionAttributeValues(expAttrVals)
					.build();
			transactionList.add(TransactWriteItem.builder()
					.update(updateJob)
					.build());
		}
	}
	
	private List<String[]> getJobKeys(String studentId,String courseId){
		Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put("PK", AttributeValue.fromS(studentId));
		expressionAttributeValues.put("SK", AttributeValue.fromS(courseId));
		Map<String,AttributeValue> job = dynamoDbClient.getItem(GetItemRequest.builder()
				                                 .tableName(CourseConstants.MONITOR_JOBS_TABLE)
				                                 .key(expressionAttributeValues)
				                                 .build()).item();
		List<String[]> jobKeys = new ArrayList<>();
		if(!job.isEmpty()) {
			List<Map<String, AttributeValue>> monitorJobs = getJobs(job.get("SK").s(),job.get("deadline").n());
			for(Map<String, AttributeValue> monitorJob : monitorJobs) {
				jobKeys.add(new String[] {monitorJob.get("PK").s(),monitorJob.get("deadline").n()});
			}
		}
		return jobKeys;
	}
	private List<Map<String, AttributeValue>> getJobs(String courseId,String deadline){
		Map<String,AttributeValue> eav = new HashMap<>();
		eav.put(":courseId", AttributeValue.fromS(courseId));
		eav.put(":deadline",AttributeValue.fromN(deadline));
		QueryRequest query = QueryRequest.builder()
				.tableName(CourseConstants.MONITOR_JOBS_TABLE)
				.indexName(CourseConstants.MONITOR_JOBS_TABLE_INDEX)
				.keyConditionExpression("SK=:courseId")
				.filterExpression("deadline = :deadline")
				.expressionAttributeValues(eav)
				.build();
		return dynamoDbClient.query(query).items();
	}
}
