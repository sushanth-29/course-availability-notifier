package com.app.notifier.course_notifier.models;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@DynamoDBTable(tableName="student_course")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentCourse {
	@DynamoDBHashKey(attributeName = "PK")
	private String PK;
	@DynamoDBRangeKey(attributeName = "SK")
	private String 	SK;
	@DynamoDBAttribute(attributeName = "first_name")
	private String firstName;
	@DynamoDBAttribute(attributeName = "last_name")
	private String lastName;
	@DynamoDBAttribute(attributeName = "phone_number")
	private String phoneNumber;
	@DynamoDBAttribute(attributeName = "email_id")
	private String emailId;
	@DynamoDBAttribute(attributeName = "username")
	private String username;
	@DynamoDBAttribute(attributeName = "password")
	private String password;
	@DynamoDBAttribute(attributeName = "status")
	private String status;
	@DynamoDBAttribute(attributeName = "enrollmentDate")
	private Long enrollmentDate;
	@DynamoDBAttribute(attributeName = "course_name")
	private String courseName;
	@DynamoDBAttribute(attributeName = "seats")
	private Integer seats;
	@DynamoDBAttribute(attributeName = "waitlist_allowed")
	private Integer waitlistThreshold;
	@DynamoDBAttribute(attributeName = "registration_last_date")
	private Long registrationEndDate;
	@DynamoDBAttribute(attributeName = "taught_by")
	private String taughtBy;
	@DynamoDBHashKey(attributeName = "PK")
	public String getPK() {
		return PK;
	}
	@DynamoDBRangeKey(attributeName = "SK")
	public String getSK() {
		return SK;
	}
}
