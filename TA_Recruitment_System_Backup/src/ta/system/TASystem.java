package ta.system;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class TASystem extends JFrame {
    private final String currentTAId = "TA001";
    private JTable jobsTable;
    private DefaultTableModel jobsTableModel;
    private DefaultTableModel statusTableModel;

    public TASystem() {
        setTitle("🏫 International College Teaching Assistant Recruitment System - TA");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 242, 255));

        JPanel headerPanel = createHeaderPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(230, 242, 255));

        tabbedPane.addTab("📋 Available Positions", createJobsPanel());
        tabbedPane.addTab("📊 My Application Status", createStatusPanel());
        tabbedPane.addTab("✏️ Edit Profile", createEditProfilePanel());
        tabbedPane.addTab("👤 View Profile", createViewProfilePanel());

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 68, 136));
        header.setPreferredSize(new Dimension(1300, 80));

        JLabel title = new JLabel("🏫 International College TA Recruitment System", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Teaching Assistant Portal", SwingConstants.CENTER);
        subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 220, 255));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(255, 200, 0)));

        return header;
    }

    // ==================== 1. 岗位面板 ====================
    private JPanel createJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 242, 255));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 242, 255));
        JLabel title = new JLabel("📋 Available Positions");
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(new Color(0, 68, 136));
        titlePanel.add(title);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"Job Title", "Module Head", "Positions", "Requirements", "AI Match Rate", "Status", "Action"};
        jobsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        loadJobsData();

        jobsTable = new JTable(jobsTableModel);
        jobsTable.setRowHeight(45);
        jobsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        jobsTable.setSelectionBackground(new Color(0, 102, 204, 50));
        jobsTable.setShowGrid(true);
        jobsTable.setGridColor(new Color(200, 215, 230));

        JTableHeader header = jobsTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(0, 68, 136));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));

        jobsTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        jobsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        jobsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        jobsTable.getColumnModel().getColumn(3).setPreferredWidth(320);
        jobsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        jobsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        jobsTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        jobsTable.getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        jobsTable.getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(jobsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(new Color(0, 68, 136), 2, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh 按钮（作为备份，但不需要使用）
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(230, 242, 255));
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton refreshBtn = new JButton("🔄 Refresh All");
        refreshBtn.setFont(new Font("微软雅黑", Font.BOLD, 12));
        refreshBtn.setBackground(new Color(100, 100, 100));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> {
            loadJobsData();
            refreshStatus();
            JOptionPane.showMessageDialog(this, "Data refreshed!");
        });
        bottomPanel.add(refreshBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadJobsData() {
        jobsTableModel.setRowCount(0);
        JSONArray jobs = FileUtil.readJSONArray("jobs.json");
        JSONArray applications = FileUtil.readJSONArray("applications.json");

        java.util.Set<String> appliedJobIds = new java.util.HashSet<>();
        for (int i = 0; i < applications.length(); i++) {
            JSONObject app = applications.getJSONObject(i);
            if (app.getString("taId").equals(currentTAId)) {
                appliedJobIds.add(app.getString("jobId"));
            }
        }

        for (int i = 0; i < jobs.length(); i++) {
            JSONObject job = jobs.getJSONObject(i);
            String jobId = job.getString("jobId");
            boolean isApplied = appliedJobIds.contains(jobId);

            String statusText = isApplied ? "✅ Applied" : "📋 Open";
            String actionText = isApplied ? "Withdraw" : "Apply";

            jobsTableModel.addRow(new Object[]{
                    job.getString("title"),
                    job.getString("moduleHead"),
                    job.getInt("positions"),
                    job.getString("requirements"),
                    job.getInt("aiMatchRate") + "%",
                    statusText,
                    actionText
            });
        }
    }

    // ==================== 2. 申请状态面板 ====================
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 242, 255));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 242, 255));
        JLabel title = new JLabel("📊 My Application Status");
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(new Color(0, 68, 136));
        titlePanel.add(title);
        panel.add(titlePanel, BorderLayout.NORTH);

        String[] columnNames = {"Job Title", "Application Time", "Status", "Operation"};
        statusTableModel = new DefaultTableModel(columnNames, 0);
        JTable statusTable = new JTable(statusTableModel);
        statusTable.setRowHeight(40);
        statusTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        statusTable.setShowGrid(true);
        statusTable.setGridColor(new Color(200, 215, 230));

        JTableHeader header = statusTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(0, 68, 136));
        header.setForeground(Color.WHITE);

        refreshStatus();

        JScrollPane scrollPane = new JScrollPane(statusTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(new Color(0, 68, 136), 2, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshStatus() {
        statusTableModel.setRowCount(0);
        JSONArray applications = FileUtil.readJSONArray("applications.json");

        for (int i = 0; i < applications.length(); i++) {
            JSONObject app = applications.getJSONObject(i);
            if (app.getString("taId").equals(currentTAId)) {
                String jobTitle = getJobTitle(app.getString("jobId"));
                String status = app.getString("status");
                String statusDisplay = "";
                if ("Under Review".equals(status)) {
                    statusDisplay = "⏳ Under Review";
                } else if ("Accepted".equals(status)) {
                    statusDisplay = "✅ Accepted";
                } else if ("Rejected".equals(status)) {
                    statusDisplay = "❌ Rejected";
                } else {
                    statusDisplay = status;
                }
                statusTableModel.addRow(new Object[]{
                        jobTitle,
                        app.getString("applyTime"),
                        statusDisplay,
                        "🔍 View Details"
                });
            }
        }
    }

    private String getJobTitle(String jobId) {
        JSONArray jobs = FileUtil.readJSONArray("jobs.json");
        for (int i = 0; i < jobs.length(); i++) {
            JSONObject job = jobs.getJSONObject(i);
            if (job.getString("jobId").equals(jobId)) {
                return job.getString("title");
            }
        }
        return jobId;
    }

    private String getJobIdByTitle(String title) {
        JSONArray jobs = FileUtil.readJSONArray("jobs.json");
        for (int i = 0; i < jobs.length(); i++) {
            JSONObject job = jobs.getJSONObject(i);
            if (job.getString("title").equals(title)) {
                return job.getString("jobId");
            }
        }
        return "";
    }

    // ==================== 3. 编辑资料面板 ====================
    private JPanel createEditProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 242, 255));
        panel.setBorder(new EmptyBorder(40, 80, 40, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JSONObject profile = FileUtil.readJSONObject("profiles.json");

        JLabel title = new JLabel("✏️ Edit My Profile");
        title.setFont(new Font("微软雅黑", Font.BOLD, 26));
        title.setForeground(new Color(0, 68, 136));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        String[][] fields = {
                {"Name:", "name", profile.optString("name", "")},
                {"Grade:", "grade", profile.optString("grade", "")},
                {"Major:", "major", profile.optString("major", "")},
                {"Email:", "email", profile.optString("email", "")},
                {"Skill Tags:", "skills", profile.optString("skills", "")}
        };

        JTextField[] textFields = new JTextField[5];
        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            JLabel label = new JLabel(fields[i][0]);
            label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            panel.add(label, gbc);
            gbc.gridx = 1;
            textFields[i] = new JTextField(25);
            textFields[i].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textFields[i].setText(fields[i][2]);
            textFields[i].setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(180, 200, 220), 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
            panel.add(textFields[i], gbc);
        }

        // Resume Upload
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel resumeLabel = new JLabel("Resume:");
        resumeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        panel.add(resumeLabel, gbc);
        gbc.gridx = 1;
        JPanel resumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        resumePanel.setBackground(new Color(230, 242, 255));
        JTextField resumeField = new JTextField(18);
        resumeField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        resumeField.setEditable(false);
        String currentResume = profile.optString("resumePath", "");
        resumeField.setText(currentResume.isEmpty() ? "No file chosen" : currentResume);
        resumeField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 200, 220), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        JButton uploadBtn = new JButton("📄 Upload CV");
        uploadBtn.setFont(new Font("微软雅黑", Font.BOLD, 12));
        uploadBtn.setBackground(new Color(0, 102, 204));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFocusPainted(false);
        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("resumes");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = "resumes/" + fileChooser.getSelectedFile().getName();
                resumeField.setText(filePath);
            }
        });
        resumePanel.add(resumeField);
        resumePanel.add(uploadBtn);
        panel.add(resumePanel, gbc);

        // Save Button
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveBtn = new JButton("💾 Save Profile");
        saveBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        saveBtn.setBackground(new Color(0, 153, 76));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(200, 50));

        JTextField nameField = textFields[0];
        JTextField gradeField = textFields[1];
        JTextField majorField = textFields[2];
        JTextField emailField = textFields[3];
        JTextField skillsField = textFields[4];

        saveBtn.addActionListener(e -> {
            JSONObject newProfile = new JSONObject();
            newProfile.put("name", nameField.getText());
            newProfile.put("grade", gradeField.getText());
            newProfile.put("major", majorField.getText());
            newProfile.put("email", emailField.getText());
            newProfile.put("skills", skillsField.getText());
            newProfile.put("resumePath", resumeField.getText());
            FileUtil.writeJSONObject("profiles.json", newProfile);
            JOptionPane.showMessageDialog(TASystem.this, "✅ Profile saved!");
        });
        panel.add(saveBtn, gbc);

        return panel;
    }

    // ==================== 4. 查看个人主页 ====================
    private JPanel createViewProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 242, 255));
        panel.setBorder(new EmptyBorder(50, 80, 50, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JSONObject profile = FileUtil.readJSONObject("profiles.json");

        JLabel title = new JLabel("👤 My Profile");
        title.setFont(new Font("微软雅黑", Font.BOLD, 28));
        title.setForeground(new Color(0, 68, 136));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0, 68, 136));
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(separator, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridwidth = 1;

        String[][] info = {
                {"📛 Name:", profile.optString("name", "Not set")},
                {"📚 Grade:", profile.optString("grade", "Not set")},
                {"🎓 Major:", profile.optString("major", "Not set")},
                {"📧 Email:", profile.optString("email", "Not set")},
                {"⚡ Skill Tags:", profile.optString("skills", "Not set")}
        };

        for (int i = 0; i < info.length; i++) {
            gbc.gridy = i + 2;
            gbc.gridx = 0;
            JLabel label = new JLabel(info[i][0]);
            label.setFont(new Font("微软雅黑", Font.BOLD, 15));
            panel.add(label, gbc);
            gbc.gridx = 1;
            JLabel value = new JLabel(info[i][1]);
            value.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            value.setForeground(new Color(60, 60, 80));
            panel.add(value, gbc);
        }

        // Resume
        gbc.gridy = 7;
        gbc.gridx = 0;
        JLabel resumeLabel = new JLabel("📄 Resume:");
        resumeLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        panel.add(resumeLabel, gbc);
        gbc.gridx = 1;
        String resumePath = profile.optString("resumePath", "");
        JButton openResumeBtn = new JButton(resumePath.isEmpty() ? "No resume uploaded" : "📄 " + new File(resumePath).getName());
        openResumeBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        openResumeBtn.setHorizontalAlignment(SwingConstants.LEFT);
        openResumeBtn.setBorderPainted(false);
        openResumeBtn.setBackground(new Color(230, 242, 255));
        openResumeBtn.setForeground(new Color(0, 102, 204));
        openResumeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (!resumePath.isEmpty()) {
            openResumeBtn.addActionListener(e -> {
                File resumeFile = new File(resumePath);
                if (resumeFile.exists()) {
                    try {
                        Desktop.getDesktop().open(resumeFile);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Cannot open file: " + resumePath);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "File not found: " + resumePath);
                }
            });
        }
        panel.add(openResumeBtn, gbc);

        return panel;
    }

    // ==================== 按钮渲染器 ====================
    private static class ActionButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ActionButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            String text = value == null ? "" : value.toString();
            setText(text);
            if ("Apply".equals(text)) {
                setBackground(new Color(0, 153, 76));
                setForeground(Color.WHITE);
                setFont(new Font("微软雅黑", Font.BOLD, 12));
            } else if ("Withdraw".equals(text)) {
                setBackground(new Color(204, 102, 0));
                setForeground(Color.WHITE);
                setFont(new Font("微软雅黑", Font.BOLD, 12));
            }
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }
    }

    // ==================== 按钮编辑器 ====================
    private class ActionButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.currentRow = row;
            label = value == null ? "" : value.toString();
            button.setText(label);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            // 移除所有旧监听器
            for (ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }

            if ("Apply".equals(label)) {
                button.setBackground(new Color(0, 153, 76));
                button.setForeground(Color.WHITE);
                button.setFont(new Font("微软雅黑", Font.BOLD, 12));
                button.addActionListener(e -> {
                    // 执行申请操作
                    String jobTitle = jobsTableModel.getValueAt(currentRow, 0).toString();
                    String jobId = getJobIdByTitle(jobTitle);

                    // 添加到 applications.json
                    JSONArray applications = FileUtil.readJSONArray("applications.json");
                    JSONObject newApp = new JSONObject();
                    newApp.put("taId", currentTAId);
                    newApp.put("jobId", jobId);
                    newApp.put("applyTime", LocalDate.now().toString());
                    newApp.put("status", "Under Review");
                    applications.put(newApp);
                    FileUtil.writeJSONArray("applications.json", applications);

                    // 立即更新这一行的状态
                    jobsTableModel.setValueAt("✅ Applied", currentRow, 5);
                    jobsTableModel.setValueAt("Withdraw", currentRow, 6);

                    // 刷新状态面板
                    refreshStatus();

                    JOptionPane.showMessageDialog(TASystem.this, "✅ Applied for: " + jobTitle);
                    fireEditingStopped();
                });
            } else if ("Withdraw".equals(label)) {
                button.setBackground(new Color(204, 102, 0));
                button.setForeground(Color.WHITE);
                button.setFont(new Font("微软雅黑", Font.BOLD, 12));
                button.addActionListener(e -> {
                    // 确认退选
                    String jobTitle = jobsTableModel.getValueAt(currentRow, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(TASystem.this,
                            "Withdraw application for:\n" + jobTitle + "?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        String jobId = getJobIdByTitle(jobTitle);

                        // 从 applications.json 中删除
                        JSONArray applications = FileUtil.readJSONArray("applications.json");
                        JSONArray newApplications = new JSONArray();
                        for (int i = 0; i < applications.length(); i++) {
                            JSONObject app = applications.getJSONObject(i);
                            if (!(app.getString("taId").equals(currentTAId) && app.getString("jobId").equals(jobId))) {
                                newApplications.put(app);
                            }
                        }
                        FileUtil.writeJSONArray("applications.json", newApplications);

                        // 立即更新这一行的状态
                        jobsTableModel.setValueAt("📋 Open", currentRow, 5);
                        jobsTableModel.setValueAt("Apply", currentRow, 6);

                        // 刷新状态面板
                        refreshStatus();

                        JOptionPane.showMessageDialog(TASystem.this, "❌ Withdrawn: " + jobTitle);
                    }
                    fireEditingStopped();
                });
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    // ==================== 主方法 ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TASystem().setVisible(true));
    }
}