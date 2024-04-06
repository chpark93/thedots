package com.ch.course.model;

import com.ch.core.model.BaseEntity;
import com.ch.core.utils.YearMonthConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.YearMonth;
import java.util.Objects;

@Entity
@Table(name = "store_courses", uniqueConstraints = {
        @UniqueConstraint(
                name="unique_store_course_yearMonth",
                columnNames={"store_id", "course_id", "year_month"}
        )
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCourse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    @Convert(converter = YearMonthConverter.class)
    @Column(name = "year_month", nullable = false)
    private YearMonth yearMonth;

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass(this) != Hibernate.getClass(o) ) return false;
        StoreCourse storeCourse = (StoreCourse) o;
        return id != null && Objects.equals(id, storeCourse.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}