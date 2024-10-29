import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
//        File oldFile = new File("E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\d1\\100.txt");
//        File newFile = new File("E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\d1\\200.txt");
//
//        if (!oldFile.exists()) {
//            System.out.println("Old file does not exist.");
//        } else if (newFile.exists()) {
//            System.out.println("New file already exists.");
//        } else if (!oldFile.renameTo(newFile)) {
//            System.out.println("Rename operation failed.");
//        } else {
//            System.out.println("Rename operation succeeded.");
//        }
        String time = "2024/09/13  19:36";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm");
        LocalDate date = LocalDate.parse(time, formatter);
        System.out.println(date);
    }
}
