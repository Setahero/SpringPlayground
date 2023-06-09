import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
	java
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.data:spring-data-jdbc")

	implementation("org.springframework.boot:spring-boot-starter-security")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
		showExceptions = true
		exceptionFormat = TestExceptionFormat.FULL
		showCauses = true
		showStandardStreams = false
		testLogging.showStackTraces = true
	}
}
