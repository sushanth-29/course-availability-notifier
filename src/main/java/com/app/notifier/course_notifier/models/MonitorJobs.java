package com.app.notifier.course_notifier.models;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
@DynamoDBTable(tableName="monitor_jobs")
@Data
public class MonitorJobs {
	@DynamoDBHashKey(attributeName = "PK")
	private String PK;
	@DynamoDBRangeKey(attributeName = "SK")
	private String SK;
	@DynamoDBAttribute(attributeName = "deadline")
	private String deadline;
	@DynamoDBHashKey(attributeName = "PK")
	public String getPK() {
		return PK;
	}
	@DynamoDBRangeKey(attributeName = "SK")
	public String getSK() {
		return SK;
	}
}
