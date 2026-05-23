package com.kxw.ta.service;

import com.kxw.ta.model.ApplicationRecord;
import com.kxw.ta.model.Job;
import com.kxw.ta.model.User;
import com.kxw.ta.store.DataStore;
import com.kxw.ta.util.SecurityUtil;
import com.kxw.ta.util.TextUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PortalService {
    private static final Set<String> JOB_TYPES = Set.of("Module Teaching", "Invigilation");
    private static final Set<String> JOB_STATUSES = Set.of("Open", "Closed");
    private static final Set<String> REVIEW_STATUSES = Set.of("Pending", "Accepted", "Rejected");

    private final DataStore store;

    public PortalService() {
        this(new DataStore());
    }

    public PortalService(DataStore store) {
        this.store = store;
    }

    public DataStore store() {
        return store;
    }

    public Optional<User> authenticate(String id, String password, String role) {
        String safeId = TextUtil.optional(id);
        String safeRole = TextUtil.optional(role);
        String safePassword = password == null ? "" : password;
        return store.findUser(safeId)
                .filter(user -> safeRole.equals(user.getRole()))
                .filter(user -> "Active".equals(user.getStatus()))
                .filter(user -> SecurityUtil.verifyPassword(safePassword, user.getPasswordHash()));
    }

    public User registerTa(String id, String password, String name, String major, String email,
            String contact, String summary, String skills) {
        id = TextUtil.required(id, "Student ID is required.");
        password = TextUtil.required(password, "Password is required.");
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        name = TextUtil.required(name, "Full name is required.");
        major = TextUtil.required(major, "Major is required.");
        email = TextUtil.required(email, "Email is required.");
        if (store.findUser(id).isPresent()) {
            throw new IllegalArgumentException("This student ID is already registered.");
        }
        User user = new User();
        user.setId(id);
        user.setPasswordHash(SecurityUtil.hashPassword(password));
        user.setRole(User.ROLE_TA);
        user.setName(name);
        user.setMajor(major);
        user.setEmail(email);
        user.setContact(TextUtil.optional(contact));
        user.setProfileSummary(TextUtil.optional(summary));
        user.setSkills(TextUtil.parseCsvLike(skills));
        user.setCreatedAt(LocalDateTime.now().toString());

        List<User> users = store.users();
        users.add(user);
        store.saveUsers(users);
        return user;
    }

    public void updateTaProfile(String taId, String name, String major, String email,
            String contact, String summary, String skills) {
        name = TextUtil.required(name, "Full name is required.");
        major = TextUtil.required(major, "Major is required.");
        email = TextUtil.required(email, "Email is required.");
        List<User> users = store.users();
        User user = users.stream().filter(item -> taId.equals(item.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Profile not found."));
        user.setName(name);
        user.setMajor(major);
        user.setEmail(email);
        user.setContact(TextUtil.optional(contact));
        user.setProfileSummary(TextUtil.optional(summary));
        user.setSkills(TextUtil.parseCsvLike(skills));
        store.saveUsers(users);
    }

    public void updateTaCv(String taId, String cvPath, String cvFileName) {
        cvPath = TextUtil.required(cvPath, "CV file path is required.");
        cvFileName = TextUtil.required(cvFileName, "CV file name is required.");
        List<User> users = store.users();
        User user = users.stream().filter(item -> taId.equals(item.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Profile not found."));
        user.setCvPath(cvPath);
        user.setCvFileName(cvFileName);
        store.saveUsers(users);
    }

    public void updateMoProfile(String moId, String name, String email, String department, String moduleName) {
        name = TextUtil.required(name, "Full name is required.");
        email = TextUtil.required(email, "Email is required.");
        List<User> users = store.users();
        User user = users.stream().filter(item -> moId.equals(item.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("MO profile not found."));
        user.setName(name);
        user.setEmail(email);
        user.setDepartment(TextUtil.optional(department));
        user.setModuleName(TextUtil.optional(moduleName));
        store.saveUsers(users);
    }

    public Job saveJob(User mo, String jobId, String title, String type, String description,
            int workloadHours, int positions, String deadline, String skills, String status) {
        if (mo == null || !User.ROLE_MO.equals(mo.getRole())) {
            throw new IllegalArgumentException("Only module organisers can save jobs.");
        }
        title = TextUtil.required(title, "Job title is required.");
        type = TextUtil.choice(type, JOB_TYPES, "Job type is invalid.");
        description = TextUtil.required(description, "Job description is required.");
        workloadHours = TextUtil.boundedInt(workloadHours, 1, 60, "Workload must be between 1 and 60 hours.");
        positions = TextUtil.boundedInt(positions, 1, 50, "Positions must be between 1 and 50.");
        LocalDate parsedDeadline = TextUtil.parseDate(deadline, "Application deadline is invalid.");
        if (parsedDeadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Application deadline cannot be in the past.");
        }
        status = status == null || status.isBlank() ? "Open" : TextUtil.choice(status, JOB_STATUSES, "Job status is invalid.");
        List<String> parsedSkills = TextUtil.parseCsvLike(skills);
        if (parsedSkills.isEmpty()) {
            throw new IllegalArgumentException("At least one required skill is needed.");
        }

        List<Job> jobs = store.jobs();
        Job job;
        String now = LocalDateTime.now().toString();
        if (jobId == null || jobId.isBlank()) {
            job = new Job();
            job.setId("JOB-" + System.currentTimeMillis());
            job.setOwnerMoId(mo.getId());
            job.setOwnerMoName(mo.getName());
            job.setCreatedAt(now);
            jobs.add(job);
        } else {
            job = jobs.stream()
                    .filter(item -> jobId.equals(item.getId()) && mo.getId().equals(item.getOwnerMoId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Job not found."));
        }
        if (positions < acceptedCount(job.getId())) {
            throw new IllegalArgumentException("Positions cannot be lower than the number of accepted applicants.");
        }

        job.setTitle(title);
        job.setType(type);
        job.setDescription(description);
        job.setWorkloadHours(workloadHours);
        job.setPositions(positions);
        job.setDeadline(parsedDeadline.toString());
        job.setRequiredSkills(parsedSkills);
        job.setStatus(status);
        job.setOwnerMoName(mo.getName());
        job.setUpdatedAt(now);
        store.saveJobs(jobs);
        return job;
    }

    public void closeJob(String moId, String jobId) {
        List<Job> jobs = store.jobs();
        Job job = jobs.stream()
                .filter(item -> jobId.equals(item.getId()) && moId.equals(item.getOwnerMoId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Job not found."));
        job.setStatus("Closed");
        job.setUpdatedAt(LocalDateTime.now().toString());
        store.saveJobs(jobs);
    }

    public void applyForJob(String taId, String jobId) {
        User ta = store.findUser(taId)
                .filter(user -> User.ROLE_TA.equals(user.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("TA account not found."));
        Job job = store.findJob(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Selected job is not available."));
        if (!"Open".equals(job.getStatus())) {
            throw new IllegalArgumentException("This job is no longer open for applications.");
        }
        if (TextUtil.parseDate(job.getDeadline(), "Selected job has an invalid deadline.").isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("This job has passed its application deadline.");
        }

        List<ApplicationRecord> applications = store.applications();
        boolean duplicate = applications.stream()
                .anyMatch(record -> ta.getId().equals(record.getTaId()) && jobId.equals(record.getJobId()));
        if (duplicate) {
            throw new IllegalArgumentException("You have already applied for this job.");
        }

        long activeCount = applications.stream()
                .filter(record -> taId.equals(record.getTaId()))
                .filter(record -> !"Rejected".equals(record.getStatus()))
                .count();
        if (activeCount >= 3) {
            throw new IllegalArgumentException("A TA may hold up to three active applications at the same time.");
        }

        ApplicationRecord record = new ApplicationRecord();
        record.setId("APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        record.setJobId(jobId);
        record.setTaId(ta.getId());
        record.setStatus("Pending");
        record.setAppliedAt(LocalDateTime.now().toString());
        applications.add(record);
        store.saveApplications(applications);
    }

    public void reviewApplication(String moId, String applicationId, String status, String note) {
        status = TextUtil.choice(status, REVIEW_STATUSES, "Review status is invalid.");
        note = TextUtil.optional(note);
        List<ApplicationRecord> applications = store.applications();
        List<Job> jobs = store.jobs();
        ApplicationRecord record = applications.stream()
                .filter(item -> applicationId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
        Job job = jobs.stream()
                .filter(item -> record.getJobId().equals(item.getId()) && moId.equals(item.getOwnerMoId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application is outside your posted jobs."));

        if ("Accepted".equals(status)) {
            long accepted = applications.stream()
                    .filter(item -> job.getId().equals(item.getJobId()))
                    .filter(item -> "Accepted".equals(item.getStatus()))
                    .filter(item -> !item.getId().equals(record.getId()))
                    .count();
            if (accepted >= job.getPositions()) {
                throw new IllegalArgumentException("All available positions for this job are already filled.");
            }
            if (accepted + 1 >= job.getPositions()) {
                job.setStatus("Closed");
                job.setUpdatedAt(LocalDateTime.now().toString());
                store.saveJobs(jobs);
            }
        }

        record.setStatus(status);
        record.setReviewNote(note);
        record.setReviewedAt(LocalDateTime.now().toString());
        store.saveApplications(applications);
    }

    public List<ApplicationView> applicationsForTa(String taId) {
        Map<String, Job> jobs = store.jobs().stream().collect(Collectors.toMap(Job::getId, job -> job));
        return store.applications().stream()
                .filter(record -> taId.equals(record.getTaId()))
                .sorted(Comparator.comparing(ApplicationRecord::getAppliedAt).reversed())
                .map(record -> new ApplicationView(record, jobs.get(record.getJobId()), null))
                .toList();
    }

    public List<ApplicationView> applicationsForMo(String moId) {
        Map<String, Job> jobs = store.jobs().stream()
                .filter(job -> moId.equals(job.getOwnerMoId()))
                .collect(Collectors.toMap(Job::getId, job -> job));
        Map<String, User> users = store.users().stream().collect(Collectors.toMap(User::getId, user -> user));
        return store.applications().stream()
                .filter(record -> jobs.containsKey(record.getJobId()))
                .sorted(Comparator.comparing(ApplicationRecord::getAppliedAt).reversed())
                .map(record -> new ApplicationView(record, jobs.get(record.getJobId()), users.get(record.getTaId())))
                .toList();
    }

    public List<Job> jobsForMo(String moId) {
        return store.jobs().stream()
                .filter(job -> moId.equals(job.getOwnerMoId()))
                .sorted(Comparator.comparing(Job::getCreatedAt, Comparator.nullsLast(String::compareTo)).reversed())
                .toList();
    }

    public Map<String, Integer> workloadByTa() {
        Map<String, Job> jobs = store.jobs().stream().collect(Collectors.toMap(Job::getId, job -> job));
        Map<String, Integer> workload = new LinkedHashMap<>();
        store.applications().stream()
                .filter(record -> "Accepted".equals(record.getStatus()))
                .forEach(record -> workload.merge(record.getTaId(),
                        Optional.ofNullable(jobs.get(record.getJobId())).map(Job::getWorkloadHours).orElse(0),
                        Integer::sum));
        return workload;
    }

    public List<User> usersByRole(String role) {
        return store.users().stream()
                .filter(user -> role.equals(user.getRole()))
                .sorted(Comparator.comparing(User::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    public List<ApplicationView> allApplicationViews() {
        Map<String, Job> jobs = store.jobs().stream().collect(Collectors.toMap(Job::getId, job -> job));
        Map<String, User> users = store.users().stream().collect(Collectors.toMap(User::getId, user -> user));
        return store.applications().stream()
                .sorted(Comparator.comparing(ApplicationRecord::getAppliedAt).reversed())
                .map(record -> new ApplicationView(record, jobs.get(record.getJobId()), users.get(record.getTaId())))
                .toList();
    }

    public int acceptedCount(String jobId) {
        return (int) store.applications().stream()
                .filter(record -> jobId.equals(record.getJobId()))
                .filter(record -> "Accepted".equals(record.getStatus()))
                .count();
    }

    public List<String> missingSkills(User ta, Job job) {
        if (ta == null || job == null) {
            return List.of();
        }
        List<String> owned = ta.getSkills().stream().map(String::toLowerCase).toList();
        List<String> missing = new ArrayList<>();
        for (String required : job.getRequiredSkills()) {
            if (!owned.contains(required.toLowerCase())) {
                missing.add(required);
            }
        }
        return missing;
    }

    public List<JobMatchView> jobMatchesForTa(String taId) {
        User ta = store.findUser(taId)
                .orElseThrow(() -> new IllegalArgumentException("TA profile not found."));
        return store.openJobs().stream()
                .map(job -> buildJobMatch(ta, job))
                .sorted(Comparator.comparing(JobMatchView::getScore).reversed()
                        .thenComparing(view -> view.getJob().getDeadline(), Comparator.nullsLast(String::compareTo)))
                .toList();
    }

    public List<ApplicantMatchView> applicantMatchesForMo(String moId) {
        Map<String, Job> jobs = store.jobs().stream()
                .filter(job -> moId.equals(job.getOwnerMoId()))
                .collect(Collectors.toMap(Job::getId, job -> job));
        Map<String, User> users = store.users().stream().collect(Collectors.toMap(User::getId, user -> user));
        return store.applications().stream()
                .filter(record -> jobs.containsKey(record.getJobId()))
                .map(record -> {
                    Job job = jobs.get(record.getJobId());
                    User ta = users.get(record.getTaId());
                    return new ApplicantMatchView(record, job, ta, buildJobMatch(ta, job));
                })
                .sorted(Comparator.comparing((ApplicantMatchView view) -> view.getMatch().getScore()).reversed()
                        .thenComparing(view -> view.getRecord().getAppliedAt(), Comparator.nullsLast(String::compareTo).reversed()))
                .toList();
    }

    private JobMatchView buildJobMatch(User ta, Job job) {
        if (ta == null || job == null) {
            return new JobMatchView(job, List.of(), List.of(), 0, "Review manually");
        }
        List<String> required = job.getRequiredSkills();
        if (required == null || required.isEmpty()) {
            return new JobMatchView(job, List.of(), List.of(), 100, "Suitable");
        }
        List<String> owned = ta.getSkills().stream().map(String::toLowerCase).toList();
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String skill : required) {
            if (owned.contains(skill.toLowerCase())) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }
        int score = (int) Math.round((matched.size() * 100.0) / required.size());
        String label;
        if (score >= 80) {
            label = "Strong match";
        } else if (score >= 50) {
            label = "Potential match";
        } else {
            label = "Skill gap";
        }
        return new JobMatchView(job, matched, missing, score, label);
    }

    public boolean canViewCv(User viewer, String cvPath) {
        if (viewer == null || cvPath == null || cvPath.isBlank()) {
            return false;
        }
        Optional<User> owner = store.users().stream()
                .filter(user -> cvPath.equals(user.getCvPath()))
                .findFirst();
        if (owner.isEmpty()) {
            return false;
        }
        if (User.ROLE_ADMIN.equals(viewer.getRole())) {
            return true;
        }
        if (User.ROLE_TA.equals(viewer.getRole())) {
            return viewer.getId().equals(owner.get().getId());
        }
        if (User.ROLE_MO.equals(viewer.getRole())) {
            Map<String, Job> jobs = store.jobs().stream()
                    .filter(job -> viewer.getId().equals(job.getOwnerMoId()))
                    .collect(Collectors.toMap(Job::getId, job -> job));
            return store.applications().stream()
                    .anyMatch(record -> owner.get().getId().equals(record.getTaId())
                            && jobs.containsKey(record.getJobId()));
        }
        return false;
    }

    public static class ApplicationView {
        private final ApplicationRecord record;
        private final Job job;
        private final User ta;

        public ApplicationView(ApplicationRecord record, Job job, User ta) {
            this.record = record;
            this.job = job;
            this.ta = ta;
        }

        public ApplicationRecord getRecord() {
            return record;
        }

        public Job getJob() {
            return job;
        }

        public User getTa() {
            return ta;
        }
    }

    public static class JobMatchView {
        private final Job job;
        private final List<String> matchedSkills;
        private final List<String> missingSkills;
        private final int score;
        private final String label;

        public JobMatchView(Job job, List<String> matchedSkills, List<String> missingSkills, int score, String label) {
            this.job = job;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
            this.score = score;
            this.label = label;
        }

        public Job getJob() {
            return job;
        }

        public List<String> getMatchedSkills() {
            return matchedSkills;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }

        public int getScore() {
            return score;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class ApplicantMatchView {
        private final ApplicationRecord record;
        private final Job job;
        private final User ta;
        private final JobMatchView match;

        public ApplicantMatchView(ApplicationRecord record, Job job, User ta, JobMatchView match) {
            this.record = record;
            this.job = job;
            this.ta = ta;
            this.match = match;
        }

        public ApplicationRecord getRecord() {
            return record;
        }

        public Job getJob() {
            return job;
        }

        public User getTa() {
            return ta;
        }

        public JobMatchView getMatch() {
            return match;
        }
    }
}
