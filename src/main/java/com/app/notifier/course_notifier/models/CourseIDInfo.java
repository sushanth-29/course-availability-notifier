package com.app.notifier.course_notifier.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Data;

@DynamoDBTable(tableName="course_id_generator")
@Data
public class CourseIDInfo{
	@DynamoDBHashKey(attributeName = "PK")
	private String courseId;
	private Integer value;
	@DynamoDBHashKey(attributeName = "PK")
	public String getCourseId() {
		return courseId;
	}
}
