package com.example.city;

import com.example.city.model.entity.*;
import com.example.city.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.github.javafaker.Faker;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceRepository serviceRepository;
    private final VisitRepository visitRepository;
    private final ReviewRepository reviewRepository;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final Faker faker = new Faker(Locale.ENGLISH);
    private final Random random = new Random();

    public DataInitializer(UserRepository userRepository,
                           CityRepository cityRepository,
                           ServiceCategoryRepository serviceCategoryRepository,
                           ServiceRepository serviceRepository,
                           VisitRepository visitRepository,
                           ReviewRepository reviewRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.visitRepository = visitRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Users
        seedUsers();

        // Seed Cities
        seedCities();

        // Seed Service Categories
        seedServiceCategories();

        // Seed Services
        seedServices();

        // Seed Visits
        seedVisits();

        // Seed Reviews
        seedReviews();
    }

    private void seedUsers() {
        if(userRepository.count() > 0) {
            return; // Data already seeded
        }

        List<User> users = new ArrayList<>();

        // Create Admin User
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .fullName("Admin User")
                .password(passwordEncoder.encode("adminpassword"))
                .role(Role.ROLE_ADMIN)
                .build();
        users.add(admin);

        // Create Regular Users
        for(int i = 1; i <= 20; i++) {
            User user = User.builder()
                    .username(faker.name().username() + i)
                    .email(faker.internet().emailAddress())
                    .fullName(faker.name().fullName())
                    .password(passwordEncoder.encode("password" + i))
                    .role(Role.ROLE_USER)
                    .build();
            users.add(user);
        }

        userRepository.saveAll(users);
        System.out.println("Seeded Users");
    }

    private void seedCities() {
        if(cityRepository.count() > 0) {
            return;
        }

        List<City> cities = new ArrayList<>();
        for(int i = 1; i <= 20; i++) {
            City city = City.builder()
                    .name(faker.address().city())
                    .country(faker.address().country())
                    .description(faker.lorem().sentence())
                    .createdAt(Instant.now())
                    .build();
            cities.add(city);
        }

        cityRepository.saveAll(cities);
        System.out.println("Seeded Cities");
    }

    private void seedServiceCategories() {
        if(serviceCategoryRepository.count() > 0) {
            return;
        }

        List<ServiceCategory> categories = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();

        int attempts = 0;
        while(categories.size() < 20 && attempts < 100) { // Limit attempts to prevent infinite loop
            String categoryName = faker.job().field();

            if(!uniqueNames.contains(categoryName)) {
                ServiceCategory category = ServiceCategory.builder()
                        .name(categoryName)
                        .description(faker.lorem().sentence())
                        .build();
                categories.add(category);
                uniqueNames.add(categoryName);
            }
            attempts++;
        }

        if(categories.size() < 20) {
            throw new RuntimeException("Could not generate 20 unique service categories after 100 attempts");
        }

        serviceCategoryRepository.saveAll(categories);
        System.out.println("Seeded Service Categories");
    }

    private void seedServices() {
        if(serviceRepository.count() > 0) {
            return;
        }

        List<Service> services = new ArrayList<>();

        List<City> cities = cityRepository.findAll();
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();

        for(int i = 1; i <= 20; i++) {
            Service service = Service.builder()
                    .category(categories.get(random.nextInt(categories.size())))
                    .city(cities.get(random.nextInt(cities.size())))
                    .name(faker.company().name())
                    .description(faker.lorem().paragraph())
                    .address(faker.address().fullAddress())
                    .contactInfo(faker.phoneNumber().phoneNumber())
                    .operatingHours("Mon - Fri: 9 AM - 5 PM")
                    .createdAt(Instant.now())
                    .build();
            services.add(service);
        }

        serviceRepository.saveAll(services);
        System.out.println("Seeded Services");
    }

    private void seedVisits() {
        List<User> users = userRepository.findAll();
        List<Service> services = serviceRepository.findAll();

        List<Visit> visits = new ArrayList<>();

        for(int i = 1; i <= 50; i++) { // More visits
            User user = users.get(random.nextInt(users.size()));
            Service service = services.get(random.nextInt(services.size()));

            // Ensure a user doesn't visit the same service multiple times
            boolean alreadyVisited = visitRepository.existsByUserIdAndServiceId(user.getId(), service.getId());
            if(alreadyVisited) {
                continue;
            }

            Visit visit = Visit.builder()
                    .user(user)
                    .service(service)
                    .visitDate(getRandomInstantIn2024())
                    .build();
            visits.add(visit);
        }

        visitRepository.saveAll(visits);
        System.out.println("Seeded Visits");
    }

    private void seedReviews() {
        List<Visit> visits = visitRepository.findAll();

        List<Review> reviews = new ArrayList<>();

        for(Visit visit : visits) {
            if(reviewRepository.existsByVisitId(visit.getId())) {
                continue;
            }

            Review review = Review.builder()
                    .visit(visit)
                    .rating((byte) (random.nextInt(5) + 1)) // 1 to 5
                    .comment(faker.lorem().sentence())
                    .createdAt(getRandomInstantIn2024())
                    .build();
            reviews.add(review);
        }

        reviewRepository.saveAll(reviews);
        System.out.println("Seeded Reviews");
    }

    /**
     * Generates a random Instant between January 1, 2024 and December 31, 2024.
     *
     * @return Instant randomly selected within the specified range.
     */
    private Instant getRandomInstantIn2024() {
        ZonedDateTime start = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime end = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 999999999, ZoneOffset.UTC);

        long startEpochSecond = start.toInstant().getEpochSecond();
        long endEpochSecond = end.toInstant().getEpochSecond();

        long randomEpochSecond = startEpochSecond + ((long) (random.nextDouble() * (endEpochSecond - startEpochSecond)));

        return Instant.ofEpochSecond(randomEpochSecond);
    }
} 