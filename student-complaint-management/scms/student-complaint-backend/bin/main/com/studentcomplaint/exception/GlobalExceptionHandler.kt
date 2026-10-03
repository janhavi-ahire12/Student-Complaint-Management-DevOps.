package com.studentcomplaint.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    private fun body(status: HttpStatus, message: String): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(status).body(mapOf<String, Any>("status" to status.value(), "message" to message))

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(e: BadRequestException) = body(HttpStatus.BAD_REQUEST, e.message ?: "Bad request")

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException) = body(HttpStatus.NOT_FOUND, e.message ?: "Not found")

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(e: ForbiddenException) = body(HttpStatus.FORBIDDEN, e.message ?: "Access denied")

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException) = body(HttpStatus.UNAUTHORIZED, e.message ?: "Unauthorized")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException) =
        body(HttpStatus.FORBIDDEN, "Access denied: you do not have permission for this action")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        val result = mapOf<String, Any>(
            "status" to 400,
            "message" to "Validation failed: " + errors.values.joinToString(", "),
            "errors" to errors
        )
        return ResponseEntity.badRequest().body(result)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(e: HttpMessageNotReadableException) =
        body(HttpStatus.BAD_REQUEST, "Malformed JSON request or invalid field value")

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException) =
        body(HttpStatus.BAD_REQUEST, "Invalid value for parameter '${e.name}'")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleIntegrity(e: DataIntegrityViolationException) =
        body(HttpStatus.BAD_REQUEST, "Operation not allowed: duplicate value or record is used by other data")

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(e: NoResourceFoundException) = body(HttpStatus.NOT_FOUND, "Endpoint not found")

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(e: HttpRequestMethodNotSupportedException) =
        body(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported for this endpoint")

    @ExceptionHandler(Exception::class)
    fun handleOther(e: Exception): ResponseEntity<Map<String, Any>> {
        log.error("Unexpected error", e)
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on the server")
    }
}
