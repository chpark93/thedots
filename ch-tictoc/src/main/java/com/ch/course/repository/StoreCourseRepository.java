package com.ch.course.repository;

import com.ch.course.model.StoreCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCourseRepository extends JpaRepository<StoreCourse, Long> {
}
