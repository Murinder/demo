package com.example.sharedlib.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation for endpoints accessible by LECTURER, DEPARTMENT_HEAD, or ADMIN.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN')")
public @interface LecturerOrAbove {
}
