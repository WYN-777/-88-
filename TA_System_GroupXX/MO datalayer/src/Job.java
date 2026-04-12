public class Job {
    public String title;       // 岗位名称: 如 Java assistant
    public String module;      // 课程模块: 如 Java Programming
    public String number;      // 招聘人数: 如 3
    public String workCircle;  // 工作周期
    public String hours;       // 每周工时
    public String emolument;   // 报酬

    public Job(String title, String module, String number, String workCircle, String hours, String emolument) {
        this.title = title;
        this.module = module;
        this.number = number;
        this.workCircle = workCircle;
        this.hours = hours;
        this.emolument = emolument;
    }
}

