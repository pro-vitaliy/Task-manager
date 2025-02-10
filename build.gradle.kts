plugins {
	java
	checkstyle
	jacoco
	id("io.freefair.lombok") version "8.11"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id ("io.sentry.jvm.gradle") version "5.1.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val activeProfile: String by project

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("net.datafaker:datafaker:2.4.2")
	implementation("org.mapstruct:mapstruct:1.6.3")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.assertj:assertj-core:3.27.2")
	testImplementation("org.instancio:instancio-junit:5.2.1")
	testImplementation("net.javacrumbs.json-unit:json-unit-spring:4.1.0")
	testImplementation("org.mockito:mockito-core:5.14.2")

	if (activeProfile == "production") {
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.4")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

if (project.hasProperty("env") && project.property("env") == "production") {
	sentry {
		includeSourceContext = true

		org = "vitaliy-oq"
		projectName = "java-spring-boot"
		authToken = System.getenv("SENTRY_AUTH_TOKEN")
	}
}

tasks.jacocoTestReport {
	reports {
		xml.required = true
		html.required = true
	}
}
