build:
	./gradlew clean build

test:
	./gradlew test

run:
	./gradlew bootRun

lint:
	./gradlew checkstyleMain

report:
	./gradlew jacocoTestReport

clean:
	./gradlew clean