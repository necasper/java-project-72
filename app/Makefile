run-dist:
	@chcp 65001 > nul
	@.\build\install\app\bin\app

build:
	@.\gradlew build

test:
	@.\gradlew test

lint:
	@.\gradlew checkstyleMain checkstyleTest

report:
	@.\gradlew jacocoTestReport

.PHONY: build