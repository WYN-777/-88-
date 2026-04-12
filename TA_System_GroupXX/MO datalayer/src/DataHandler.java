import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class DataHandler {
    private final String filePath = "data/jobs.csv";
    // 对应 MO-001: 发布岗位数据存储
    public void saveJob(Job job) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data/jobs.csv", true)))) {
            // 将岗位信息拼接成一行存入 CSV [cite: 43]
            out.println(job.title + "," + job.module + "," + job.number + "," + job.workCircle);
            System.out.println("数据成功存入文件！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Job> getAllJobs() {
        List<Job> jobList = new ArrayList<>();
        File file = new File(filePath);

        // 如果文件还不存在，直接返回空列表
        if (!file.exists()) {
            return jobList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 将 CSV 的一行按逗号切割 [cite: 127-139]
                String[] data = line.split(",");
                if (data.length >= 6) {
                    // 重新组装成 Job 对象
                    Job job = new Job(data[0], data[1], data[2], data[3], data[4], data[5]);
                    jobList.add(job);
                }
            }
        } catch (IOException e) {
            System.err.println("读取失败: " + e.getMessage());
        }
        return jobList;
    }
}
