package com.kxw.ta.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kxw.ta.model.ApplicationRecord;
import com.kxw.ta.model.Job;
import com.kxw.ta.model.User;
import com.kxw.ta.util.SecurityUtil;
import com.kxw.ta.util.TextUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DataStore {
    private static final Object LOCK = new Object();
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final Path dataDir;
    private final Path uploadsDir;
    private final Path usersFile;
    private final Path jobsFile;
    private final Path applicationsFile;

    public DataStore() {
        this(Paths.get(System.getProperty("app.dataDir", "data")));
    }

    public DataStore(Path dataDir) {
        this.dataDir = dataDir;
        this.uploadsDir = dataDir.resolve("uploads");
        this.usersFile = dataDir.resolve("users.json");
        this.jobsFile = dataDir.resolve("jobs.json");
        this.applicationsFile = dataDir.resolve("applications.json");
        ensureReady();
    }

    public Path getUploadsDir() {
        return uploadsDir;
    }

    public List<User> users() {
        synchronized (LOCK) {
            return readList(usersFile, new TypeReference<>() {});
        }
    }

    public void saveUsers(List<User> users) {
        synchronized (LOCK) {
            writeList(usersFile, users);
        }
    }

    public List<Job> jobs() {
        synchronized (LOCK) {
            return readList(jobsFile, new TypeReference<>() {});
        }
    }

    public void saveJobs(List<Job> jobs) {
        synchronized (LOCK) {
            writeList(jobsFile, jobs);
        }
    }

    public List<ApplicationRecord> applications() {
        synchronized (LOCK) {
            return readList(applicationsFile, new TypeReference<>() {});
        }
    }

    public void saveApplications(List<ApplicationRecord> applications) {
        synchronized (LOCK) {
            writeList(applicationsFile, applications);
        }
    }

    public Optional<User> findUser(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return users().stream().filter(user -> id.equalsIgnoreCase(user.getId())).findFirst();
    }

    public Optional<Job> findJob(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return jobs().stream().filter(job -> id.equals(job.getId())).findFirst();
    }

    public Optional<ApplicationRecord> findApplication(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return applications().stream().filter(record -> id.equals(record.getId())).findFirst();
    }

    public List<Job> openJobs() {
        LocalDate today = LocalDate.now();
        return jobs().stream()
                .filter(job -> "Open".equals(job.getStatus()))
                .filter(job -> {
                    try {
                        return !TextUtil.parseDate(job.getDeadline(), "Invalid deadline.").isBefore(today);
                    } catch (IllegalArgumentException ex) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(Job::getDeadline, Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    private void ensureReady() {
        try {
            Files.createDirectories(dataDir);
            Files.createDirectories(uploadsDir);
            if (Files.notExists(usersFile)) {
                writeList(usersFile, seedUsers());
            }
            if (Files.notExists(jobsFile)) {
                writeList(jobsFile, seedJobs());
            }
            if (Files.notExists(applicationsFile)) {
                writeList(applicationsFile, new ArrayList<ApplicationRecord>());
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to initialise JSON storage", ex);
        }
    }

    private List<User> seedUsers() {
        List<User> users = new ArrayList<>();
        String now = LocalDateTime.now().toString();

        User admin = new User();
        admin.setId("admin");
        admin.setPasswordHash(SecurityUtil.hashPassword("admin123"));
        admin.setRole(User.ROLE_ADMIN);
        admin.setName("School Administrator");
        admin.setEmail("admin@bupt-is.edu");
        admin.setDepartment("International School");
        admin.setCreatedAt(now);
        users.add(admin);

        User mo = new User();
        mo.setId("mo1001");
        mo.setPasswordHash(SecurityUtil.hashPassword("mo123456"));
        mo.setRole(User.ROLE_MO);
        mo.setName("Dr. Emily Carter");
        mo.setEmail("emily.carter@bupt-is.edu");
        mo.setDepartment("Computer Science");
        mo.setModuleName("Software Engineering");
        mo.setCreatedAt(now);
        users.add(mo);

        User ta = new User();
        ta.setId("20260001");
        ta.setPasswordHash(SecurityUtil.hashPassword("ta123456"));
        ta.setRole(User.ROLE_TA);
        ta.setName("Alex Chen");
        ta.setEmail("alex.chen@student.bupt-is.edu");
        ta.setMajor("Software Engineering");
        ta.setContact("alex.chen@student.bupt-is.edu");
        ta.setProfileSummary("Experienced in Java, web development, and peer tutoring.");
        ta.setSkills(List.of("Java", "Servlet", "Testing", "Communication"));
        ta.setCreatedAt(now);
        users.add(ta);

        return users;
    }

    private List<Job> seedJobs() {
        String now = LocalDateTime.now().toString();
        String deadline = LocalDate.now().plusDays(30).toString();

        Job teaching = new Job();
        teaching.setId("JOB-1001");
        teaching.setOwnerMoId("mo1001");
        teaching.setOwnerMoName("Dr. Emily Carter");
        teaching.setTitle("Software Engineering Lab Assistant");
        teaching.setType("Module Teaching");
        teaching.setDescription("Support weekly labs, answer student questions, and help prepare practical materials.");
        teaching.setWorkloadHours(8);
        teaching.setPositions(2);
        teaching.setDeadline(deadline);
        teaching.setStatus("Open");
        teaching.setRequiredSkills(List.of("Java", "Testing", "Communication"));
        teaching.setCreatedAt(now);
        teaching.setUpdatedAt(now);

        Job invigilation = new Job();
        invigilation.setId("JOB-1002");
        invigilation.setOwnerMoId("mo1001");
        invigilation.setOwnerMoName("Dr. Emily Carter");
        invigilation.setTitle("Midterm Invigilation Assistant");
        invigilation.setType("Invigilation");
        invigilation.setDescription("Assist exam setup, attendance checks, room monitoring, and paper collection.");
        invigilation.setWorkloadHours(4);
        invigilation.setPositions(4);
        invigilation.setDeadline(deadline);
        invigilation.setStatus("Open");
        invigilation.setRequiredSkills(List.of("Organisation", "Attention to Detail", "Communication"));
        invigilation.setCreatedAt(now);
        invigilation.setUpdatedAt(now);

        return List.of(teaching, invigilation);
    }

    private <T> List<T> readList(Path file, TypeReference<List<T>> type) {
        try {
            if (Files.notExists(file) || Files.size(file) == 0) {
                return new ArrayList<>();
            }
            return new ArrayList<>(MAPPER.readValue(file.toFile(), type));
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read " + file.getFileName(), ex);
        }
    }

    private void writeList(Path file, Object value) {
        try {
            MAPPER.writeValue(file.toFile(), value);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write " + file.getFileName(), ex);
        }
    }
}
