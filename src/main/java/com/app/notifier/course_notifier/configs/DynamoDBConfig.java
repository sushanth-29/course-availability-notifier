package com.app.notifier.course_notifier.configs;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
@Configuration
@EnableDynamoDBRepositories
public class DynamoDBConfig {
	@Value("${aws.dynaomodb.endpoint}")
	private String amazonDynamoDBEndPoint;
	@Value("${aws.dynamodb.accessKey}")
	private String amazonAWSAccessKey;
	@Value("${aws.dynamodb.secretKey}")
	private String amazonAWSSecretKey;
	@Value("${aws.region}")
	private String awsRegeion;
	@Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(amazonDynanamoDB());
    }
	@Bean
	public AmazonDynamoDB amazonDynanamoDB() {
		return AmazonDynamoDBClientBuilder.standard()
				  .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndPoint, awsRegeion))
				  .withCredentials(amazomAWSCredentialsProvider())
				  .build();
				  
	}
	public AWSCredentialsProvider amazomAWSCredentialsProvider() {
		return new AWSStaticCredentialsProvider(amazonAWSCredentials());
	}
	@Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(
          amazonAWSAccessKey, amazonAWSSecretKey);
    }
	/*
	 * Enhanced Configuration
	*/
	public AwsCredentials amaAwsCredentialsEnhanced(){
		return AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
	}
	public AwsCredentialsProvider amazonAWSCredentialsProviderEnhanced() {
		return StaticCredentialsProvider.create(amaAwsCredentialsEnhanced());
	}
	@Bean
	public DynamoDbClient dynamoDbClient(){
		return DynamoDbClient.builder()
				.region(Region.US_EAST_1)
				.credentialsProvider(amazonAWSCredentialsProviderEnhanced())
				.build();
	}
	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
		return DynamoDbEnhancedClient.builder()
				.dynamoDbClient(dynamoDbClient())
				.build();
	}
	
}
