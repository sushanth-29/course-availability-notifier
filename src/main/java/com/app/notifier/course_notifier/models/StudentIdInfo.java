package com.app.notifier.course_notifier.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
@DynamoDBTable(tableName="Student_ID_Generator")
@Data
public class StudentIdInfo {
	@DynamoDBHashKey(attributeName = "PK")
	private String PK;
	private int value;
	@DynamoDBHashKey(attributeName = "PK")
	public String getPK() {
		return PK;
	}
}
