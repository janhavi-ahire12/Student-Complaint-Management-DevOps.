package com.studentcomplaint

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudentComplaintApplication

fun main(args: Array<String>) {
    runApplication<StudentComplaintApplication>(*args)
}
