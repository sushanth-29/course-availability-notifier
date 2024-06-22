package com.app.notifier.course_notifier.constants;

public class CourseConstants {
	private CourseConstants() {}
	public final static String COURSE_ID_PREFIX="course#";
	public final static String COURSE_ID_GENERATOR_PK = "course_id";
	public final static Integer COURSE_START_ID_INDEX=7;
	public final static String  ENROLLED ="E";
	public final static String  WAITLIST_STATUS ="W";
	public final static String  INTERESTED_STATUS ="I";
	public final static String  STUDENT_COURSE_TABLE = "student_course";
	public final static String PK = "PK";
	public final static String SK = "SK";
	public final static String STATUS = "status";
	public final static String ENROLLMENT_TIME = "enrollmentDate";
	public final static String MONITOR_JOBS_TABLE = "monitor_jobs";
	public final static String MONITOR_JOBS_TABLE_INDEX = "monitor_jobs_GSI";
}
