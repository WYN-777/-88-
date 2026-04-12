import java.util.List;
public class TestDatalayer {
    public static void main(String[] args) {
        DataHandler dh = new DataHandler();
        
        System.out.println("--- 开始读取测试 ---");
        List<Job> allJobs = dh.getAllJobs();
        
        if (allJobs.isEmpty()) {
            System.out.println("警告：没读到任何数据，请检查 jobs.csv 是否有内容。");
        } else {
            for (Job j : allJobs) {
                System.out.println("读取成功！岗位名称: " + j.title + " | 课程: " + j.module);
            }
        }
    }
}
