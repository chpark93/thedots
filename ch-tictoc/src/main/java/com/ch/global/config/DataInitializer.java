package com.ch.global.config;

import com.ch.course.model.Address;
import com.ch.course.model.Course;
import com.ch.course.model.Store;
import com.ch.course.model.StoreCourse;
import com.ch.course.repository.CourseRepository;
import com.ch.course.repository.StoreCourseRepository;
import com.ch.course.repository.StoreRepository;
import com.ch.user.model.User;
import com.ch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CourseRepository courseRepository;
    private final StoreCourseRepository storeCourseRepository;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.deleteAll();
        storeRepository.deleteAll();
        courseRepository.deleteAll();
        storeCourseRepository.deleteAll();

        // Initializing Dummy Datas
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .name("홍길동")
                .email("email1@test.com")
                .build());
        users.add(User.builder()
                .name("마징가")
                .email("email2@test.com")
                .build());
        users.add(User.builder()
                .name("조조")
                .email("email3@test.com")
                .build());
        users.add(User.builder()
                .name("유비")
                .email("email4@test.com")
                .build());
        users.add(User.builder()
                .name("황충")
                .email("email5@test.com")
                .build());

        List<Store> stores = new ArrayList<>();
        stores.add(Store.builder()
                .name("잠실점")
                .address(Address.builder()
                        .city("잠실")
                        .street("길거리")
                        .zipCode("100")
                        .build()
                )
                .build());
        stores.add(Store.builder()
                .name("잠실점")
                .address(Address.builder()
                        .city("잠실")
                        .street("길거리")
                        .zipCode("100")
                        .build()
                )
                .build());
        stores.add(Store.builder()
                .name("판교점")
                .address(Address.builder()
                        .city("판교")
                        .street("길거리")
                        .zipCode("100")
                        .build()
                )
                .build());

        List<Course> courses = new ArrayList<>();
        courses.add(Course.builder().name("도시농부").count(20).build());
        courses.add(Course.builder().name("드로잉").count(20).build());
        courses.add(Course.builder().name("오감놀이").count(20).build());

        List<StoreCourse> storeCourses = new ArrayList<>();
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(0))
                .course(courses.get(0))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(0))
                .course(courses.get(1))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(0))
                .course(courses.get(2))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(1))
                .course(courses.get(0))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(1))
                .course(courses.get(1))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(1))
                .course(courses.get(2))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(2))
                .course(courses.get(0))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(2))
                .course(courses.get(1))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );
        storeCourses.add(StoreCourse.builder()
                .store(stores.get(2))
                .course(courses.get(2))
                .yearMonth(YearMonth.from(LocalDate.now()))
                .build()
        );

        // Save Dummy Datas
        userRepository.saveAll(users);
        storeRepository.saveAll(stores);
        courseRepository.saveAll(courses);
        storeCourseRepository.saveAll(storeCourses);

    }
}
