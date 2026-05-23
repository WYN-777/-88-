package com.kxw.ta.model;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private String id;
    private String ownerMoId;
    private String ownerMoName;
    private String title;
    private String type;
    private String description;
    private int workloadHours;
    private int positions = 1;
    private String deadline;
    private String status = "Open";
    private String createdAt;
    private String updatedAt;
    private List<String> requiredSkills = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerMoId() {
        return ownerMoId;
    }

    public void setOwnerMoId(String ownerMoId) {
        this.ownerMoId = ownerMoId;
    }

    public String getOwnerMoName() {
        return ownerMoName;
    }

    public void setOwnerMoName(String ownerMoName) {
        this.ownerMoName = ownerMoName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWorkloadHours() {
        return workloadHours;
    }

    public void setWorkloadHours(int workloadHours) {
        this.workloadHours = workloadHours;
    }

    public int getPositions() {
        return positions;
    }

    public void setPositions(int positions) {
        this.positions = positions;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills == null ? new ArrayList<>() : requiredSkills;
    }
}
