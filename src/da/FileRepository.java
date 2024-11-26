package da;


import utils.SimpleUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static utils.CommandLineFont.*;
import static utils.KeyUtils.generateSecretKey;
import static utils.SimpleUtils.pressEnterToContinue;

/*
*FileRepository 提供底层文件系统操作的实现，直接与文件系统交互，
* 包括判断路径是否合法、创建文件夹、删除文件夹等。
*
* */
public class FileRepository {

    protected final String secretKey = generateSecretKey();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private static final Map<String, String> mimeTypeToChinese = new HashMap<>();

    static {
        mimeTypeToChinese.put("text/plain", "文本文件");
        mimeTypeToChinese.put("application/pdf", "PDF文件");
        mimeTypeToChinese.put("image/jpeg", "JPEG图片");
        mimeTypeToChinese.put("image/png", "PNG图片");
        mimeTypeToChinese.put("application/zip", "ZIP压缩文件");
        mimeTypeToChinese.put("application/x-tar", "TAR压缩文件");
        mimeTypeToChinese.put("application/x-gzip", "GZIP压缩文件");
        mimeTypeToChinese.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel文件");
        mimeTypeToChinese.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Word文件");
    }

    private String getChineseFileType(String mimeType) {
        return mimeTypeToChinese.getOrDefault(mimeType, "未知文件类型");
    }

    // 判断路径是否为目录
    public boolean isDirectory(String path) {
        File directory = new File(path);

        return directory.exists() && directory.isDirectory();
    }

    // 创建目录
    public void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            System.out.println(created ? "文件夹创建成功" : "文件夹创建失败");

        } else {
            System.out.println("文件夹已存在");
        }
    }

    // 删除目录
    public void deleteDirectory(String path) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            if (directory.listFiles().length >0) {
                System.out.println("文件夹非空，请确认是否删除(y/n)");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("n")) {
                    System.out.println("取消删除");
                    return;
                }
                if(input.equalsIgnoreCase("y")){
                    System.out.println("确认删除");
                    deleteDirectoryFile(directory);
                }
            }else{
                boolean deleted = directory.delete();
                System.out.println(deleted ? "文件夹删除成功" : "文件夹删除失败");
            }

        } else {
            System.out.println("目录不存在");
        }
    }
    private void deleteDirectoryFile(File directory){
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryFile(file);
                } else {
                    file.delete();
                }
            }
        }
        boolean deleted = directory.delete();
        System.out.println(deleted ? "文件夹删除成功" : "文件夹删除失败");

    }

    // 列出目录内容
    public void listFiles(String path) {
        File directory = new File(path);


        File[] files = directory.listFiles();
        if (files != null) {
            System.out.println("-".repeat(80));
            System.out.printf("|%-27s|%-8s|%-18s|%-18s|%n", "最后修改时间", "大小", "文件名", "文件类型");
            for (File file : files) {
                long lastModified = file.lastModified();
                Instant instant = Instant.ofEpochMilli(lastModified);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm")
                        .withLocale(Locale.getDefault())
                        .withZone(ZoneId.systemDefault());
                String formattedTime = formatter.format(instant);
                if (file.isDirectory()) {
                    System.out.printf("|%-30s|%-10s|%-20s|%-20s|%n", formattedTime, "", file.getName(),"文件夹" );
                } else {
                    System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),
                            getChineseFileType(URLConnection.guessContentTypeFromName(file.getName())));
                }

            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }
    // 列出目录内容
    public void listFiles(String command,String path) {
        File directory = new File(path);



        File[] files = directory.listFiles();
        if (files != null) {
            System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");
            //排序，按照命令中的参数排序，如果\ts这种，先按照t排序，再按照s排序
            //如果是-t命令,按时间排序
            if (command.contains("t")){
                if(command.contains("r")){
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                }else {
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                }

            }
            //如果是-s命令,按大小排序
            else if (command.contains("s")) {
                if(command.contains("r")){
                    Arrays.sort(files, Comparator.comparingLong(File::length).reversed());
                }else {
                    Arrays.sort(files, Comparator.comparingLong(File::length));
                }
            }
            //如果是-n命令,按文件名排序
            else if (command.contains("n")) {

                if(command.contains("r")){
                    Arrays.sort(files, Comparator.comparing(File::getName).reversed());
                }else {
                    Arrays.sort(files, Comparator.comparing(File::getName));
                }
            }

            for (File file : files) {
                long lastModified = file.lastModified();
                Instant instant = Instant.ofEpochMilli(lastModified);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm")
                        .withLocale(Locale.getDefault())
                        .withZone(ZoneId.systemDefault());
                String formattedTime = formatter.format(instant);
                if (file.isDirectory()) {
                    System.out.printf("|%-30s|%-10s|%-20s|%-20s|%n", formattedTime, "", file.getName(),"文件夹" );
                } else {
                    System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),
                            getChineseFileType(URLConnection.guessContentTypeFromName(file.getName())));
                }

            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }

    // 列出目录内容,保证通配符能执行，列出符合条件的文件
    public void listFilesByPattern(String path) {

        // 先获取path的父目录
        String parentPath = path.substring(0, path.lastIndexOf(File.separator));
        // 获取path的文件名
        String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
        // 将通配符转换为正则表达式
        fileName = fileName.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        File directory = new File(parentPath);

        File[] files = directory.listFiles();
        if (files != null) {
            System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");
            for (File file : files) {
                if (file.getName().matches(fileName)) {
                    long lastModified = file.lastModified();
                    Instant instant = Instant.ofEpochMilli(lastModified);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm")
                            .withLocale(Locale.getDefault())
                            .withZone(ZoneId.systemDefault());
                    String formattedTime = formatter.format(instant);
                    if (file.isDirectory()) {
                        System.out.printf("|%-30s|%-10s|%-20s|%-20s|%n", formattedTime, "", file.getName(),"文件夹" );
                    } else {
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),
                                getChineseFileType(URLConnection.guessContentTypeFromName(file.getName())));
                    }
                }
            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }
    // 列出目录内容,保证通配符能执行，列出符合条件的文件
    public void listFilesByPattern(String command,String path) {

        // 先获取path的父目录
        String parentPath = path.substring(0, path.lastIndexOf(File.separator));
        // 获取path的文件名
        String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
        // 将通配符转换为正则表达式
        fileName = fileName.replace(".", "\\.").replace("*", ".*").replace("?", ".");
        File directory = new File(parentPath);
        System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");
        File[] files = directory.listFiles();
        if (files != null) {
            //如果是-t命令,按时间排序
            if (command.contains("t")){
                if (command.contains("r")) {
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                } else {
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                }
            }
            //如果是-s命令,按大小排序
            else if (command.contains("s")) {
                if(command.contains("r")){
                    Arrays.sort(files, Comparator.comparingLong(File::length).reversed());
                }else {
                    Arrays.sort(files, Comparator.comparingLong(File::length));
                }
            }
            //如果是-n命令,按文件名排序
            else if (command.contains("n")) {
                if(command.contains("r")){
                    Arrays.sort(files, Comparator.comparing(File::getName).reversed());
                }else {
                    Arrays.sort(files, Comparator.comparing(File::getName));
                }
            }
            for (File file : files) {
                if (file.getName().matches(fileName)) {
                    long lastModified = file.lastModified();
                    Instant instant = Instant.ofEpochMilli(lastModified);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm")
                            .withLocale(Locale.getDefault())
                            .withZone(ZoneId.systemDefault());
                    String formattedTime = formatter.format(instant);
                    if (file.isDirectory()) {
                        System.out.printf("|%-30s|%-10s|%-20s|%-20s|%n", formattedTime, "", file.getName(),"文件夹" );
                    } else {
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),
                                getChineseFileType(URLConnection.guessContentTypeFromName(file.getName())));
                    }
                }
            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }
    // 列出目录内容,保证时间在指定范围内
    public void listFilesByTime(String path, String startTime, String endTime) {
        File directory = new File(path);
        System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");
        File[] files = directory.listFiles();
        if (files != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());
            Instant startInstant = Instant.from(formatter.parse(startTime));
            Instant endInstant = Instant.from(formatter.parse(endTime));
            for (File file : files) {
                long lastModified = file.lastModified();
                Instant fileInstant = Instant.ofEpochMilli(lastModified);
                if (fileInstant.isAfter(startInstant) && fileInstant.isBefore(endInstant)) {
                    String formattedTime = formatter.format(fileInstant);
                    if (file.isDirectory()) {
                        System.out.printf("|%-30s|%-10s|%-20s|%-20s|%n", formattedTime, "", file.getName(), "文件夹");
                    } else {
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),
                                getChineseFileType(URLConnection.guessContentTypeFromName(file.getName())));
                    }
                }
            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }
    //列出目录内容,保证文件大小在指定范围内
    public void listFilesBySize(String path,String minSize,String maxSize){
        File directory = new File(path);
        System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");
        File[] files = directory.listFiles();
        if (files != null) {
            long min = Long.parseLong(minSize);
            long max = Long.parseLong(maxSize);
            for (File file : files) {
                long size = file.length();
                if (size >= min && size <= max) {
                    long lastModified = file.lastModified();
                    Instant instant = Instant.ofEpochMilli(lastModified);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm")
                            .withLocale(Locale.getDefault())
                            .withZone(ZoneId.systemDefault());
                    String formattedTime = formatter.format(instant);
                    if (file.isDirectory()) {
                        System.out.printf("|%-30s|%-10s|%-20s|%-20s|%n", formattedTime, "", file.getName(), "文件夹");
                    } else {
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),
                                getChineseFileType(URLConnection.guessContentTypeFromName(file.getName())));
                    }
                }
            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }


    // 创建文件
    public void createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.createNewFile()) {
                System.out.println("文件创建成功: " + filePath);
            } else {
                System.out.println("文件已存在: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("文件创建失败: " + e.getMessage());
        }
    }


    // 删除文件
    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("文件删除成功: " + filePath);
            } else {
                System.out.println("文件删除失败");
            }
        } else {
            System.out.println("文件不存在: " + filePath);
        }
    }
    // 写入文件内容
    public void writeFile(String content,Path path) {
        try {
            if (!Files.exists(path)) {
                System.out.println("文件不存在，是否创建文件(y/n): " + path);
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("n")) {
                    System.out.println("取消创建文件");
                    return;
                }
                if (input.equalsIgnoreCase("y")) {
                    Files.createFile(path);
                }
            }
            Files.write(path, content.getBytes());
            System.out.println("文件写入成功: " + path);
        } catch (IOException e) {
            System.out.println("文件写入失败: " + e.getMessage());
        }
    }

    // 读取文件内容
    public void readFileContent(Path path) {
//        Path path = Paths.get(filePath);
        try {
            if (!Files.exists(path)) {
                System.out.println("文件不存在: " + path);
                return;
            }
            Files.lines(path).forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
        }
    }

    // 重命名文件
    public void renameFile(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        if (oldFile.exists() && oldFile.renameTo(newFile)) {
            System.out.println("文件重命名成功: " + newFilePath);
        } else {
            System.out.println("文件重命名失败");
        }
    }

//    // 复制文件
//    public void copyFile(Path sourcePath, Path destinationPath) {
//
//        try {
//            long startTime = System.currentTimeMillis();
//            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
//            long endTime = System.currentTimeMillis();
//            System.out.println("文件复制成功: " + destinationPath);
//            System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
//        } catch (IOException e) {
//            System.out.println("文件复制失败: " + e.getMessage());
//        }
//    }
//    // 拷贝文件夹（深度拷贝）：包括文件夹中的所有文件
//    public void copyDirectory(Path sourcePath, Path destinationPath) {
//        try {
//            long startTime = System.currentTimeMillis();
//            Files.walk(sourcePath)
//                    .forEach(source -> {
//                        Path destination = destinationPath.resolve(sourcePath.relativize(source));
//                        try {
//                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
//                        } catch (IOException e) {
//                            System.out.println("文件复制失败: " + e.getMessage());
//                        }
//                    });
//            long endTime = System.currentTimeMillis();
//            System.out.println("文件夹复制成功: " + destinationPath);
//            System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
//        } catch (IOException e) {
//            System.out.println("文件夹复制失败: " + e.getMessage());
//        }
//    }
    // 复制操作
    // 选择前台或后台模式执行文件拷贝
    public void copyFile(Path sourcePath, Path destinationPath, boolean async) {
        if (async) {
            executorService.submit(() -> copyFile(sourcePath, destinationPath));
        } else {
            try {
                long totalBytes = Files.size(sourcePath);   // 文件大小
                long copiedBytes = 0;               // 已复制的字节数
                long startTime = System.currentTimeMillis();    // 开始时间

                // 使用 try-with-resources 语句创建输入输出流
                try (InputStream in = Files.newInputStream(sourcePath);
                     OutputStream out = Files.newOutputStream(destinationPath)) {
                    byte[] buffer = new byte[1024];    // 缓冲区
                    int bytesRead;                    // 每次读取的字节数
                    // 读取文件内容并写入目标文件
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        copiedBytes += bytesRead;
                        SimpleUtils.showProgressBar(copiedBytes, totalBytes);
                    }
                }

                long endTime = System.currentTimeMillis();
                System.out.println("文件复制成功: " + destinationPath);
                System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
            } catch (IOException e) {
                System.out.println("文件复制失败: " + e.getMessage());
            }
        }
    }


    // 复制文件 后台
    public void copyFile(Path sourcePath, Path destinationPath) {
        try {
            long totalBytes = Files.size(sourcePath);   // 文件大小
            long copiedBytes = 0;               // 已复制的字节数
            long startTime = System.currentTimeMillis();    // 开始时间

            // 使用 try-with-resources 语句创建输入输出流
            try (InputStream in = Files.newInputStream(sourcePath);
                 OutputStream out = Files.newOutputStream(destinationPath)) {
                byte[] buffer = new byte[1024];    // 缓冲区
                int bytesRead;                    // 每次读取的字节数
                // 读取文件内容并写入目标文件
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    copiedBytes += bytesRead;
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println(ANSI_GREEN+"-------------------------------");
            System.out.println("异步任务完成");
            System.out.println("文件复制成功: " + destinationPath);
            System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
            System.out.println("-------------------------------"+ANSI_RESET);
        } catch (IOException e) {
            System.out.println("文件复制失败: " + e.getMessage());
        }
    }
    // 选择前台或后台模式执行文件夹深度拷贝
    public void copyDirectory(Path sourcePath, Path destinationPath, boolean async) {
        if (async) {
            executorService.submit(() -> copyDirectory(sourcePath, destinationPath));
        } else {
            try {
                // 计算文件夹大小
                long totalBytes = Files.walk(sourcePath)
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                return Files.size(p);
                            } catch (IOException e) {
                                return 0L;
                            }
                        }).sum();
                // 已复制的字节数,必须是原子类型，因为为了保证线程安全
                AtomicLong copiedBytes = new AtomicLong();
                long startTime = System.currentTimeMillis();    // 开始时间
                // 遍历源文件夹
                Files.walk(sourcePath).forEach(source -> {
                    Path destination = destinationPath.resolve(sourcePath.relativize(source));
                    // 复制文件或创建目录
                    try {
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else {
                            try (InputStream in = Files.newInputStream(source);
                                 OutputStream out = Files.newOutputStream(destination)) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, bytesRead);
                                    copiedBytes.addAndGet(bytesRead);
                                    SimpleUtils.showProgressBar(copiedBytes.get(), totalBytes);
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("文件复制失败: " + e.getMessage());
                    }
                });

                long endTime = System.currentTimeMillis();
                System.out.println("文件夹复制成功: " + destinationPath);
                System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
            } catch (IOException e) {
                System.out.println("文件夹复制失败: " + e.getMessage());
            }
        }
    }

    // 拷贝文件夹（深度拷贝）：包括文件夹中的所有文件 后台
    private void copyDirectory(Path sourcePath, Path destinationPath) {
        try {
            // 计算文件夹大小
            long totalBytes = Files.walk(sourcePath)
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    }).sum();
            // 已复制的字节数,必须是原子类型，因为为了保证线程安全
            AtomicLong copiedBytes = new AtomicLong();
            long startTime = System.currentTimeMillis();    // 开始时间
            // 遍历源文件夹
            Files.walk(sourcePath).forEach(source -> {
                Path destination = destinationPath.resolve(sourcePath.relativize(source));
                // 复制文件或创建目录
                try {
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                    } else {
                        try (InputStream in = Files.newInputStream(source);
                             OutputStream out = Files.newOutputStream(destination)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                                copiedBytes.addAndGet(bytesRead);

                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("文件复制失败: " + e.getMessage());
                }
            });

            long endTime = System.currentTimeMillis();
            System.out.println(ANSI_GREEN+"-------------------------------");
            System.out.println("异步任务完成");
            System.out.println("文件夹复制成功: " + destinationPath);
            System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
            System.out.println("-------------------------------"+ANSI_RESET);

        } catch (IOException e) {
            System.out.println("文件夹复制失败: " + e.getMessage());
        }
    }
    // 移动文件
    public void moveFile(Path sourcePath, Path destinationPath) {
        try {
            //如果目标路径不存在，创建新的文件夹
            if(!Files.exists(destinationPath)){
                System.out.println("文件夹不存在，是否创建新的文件夹(y/n)");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("n")) {
                    System.out.println("取消移动");
                    return;
                }
                if(input.equalsIgnoreCase("y")){
                    Files.createDirectories(destinationPath);
                }
            }

            // 移动文件,由于destinationPath是文件夹，所以需要加上文件名
            destinationPath = destinationPath.resolve(sourcePath.getFileName());

            //如果目标路径文件已经存在，是否覆盖
            if(Files.exists(destinationPath)){
                System.out.println("文件已存在，是否覆盖(y/n)");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("n")) {
                    System.out.println("取消移动");
                    return;
                }
                if(input.equalsIgnoreCase("y")){
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("文件移动成功: " + destinationPath);
                }
            }else{
                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("文件移动成功: " + destinationPath);
            }

        } catch (IOException e) {
            System.out.println("文件移动失败: " + e.getMessage());
        }
    }

    //加密操作

    // 加密文件
    public void encryptFile(String sourcePath, String destinationPath) {
        try {
            SecretKey secretKey = decodeKeyFromString(this.secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (FileInputStream fis = new FileInputStream(sourcePath);
                 FileOutputStream fos = new FileOutputStream(destinationPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    byte[] output = cipher.update(buffer, 0, length);
                    if (output != null) {
                        fos.write(output);
                    }
                }
                byte[] output = cipher.doFinal();
                if (output != null) {
                    fos.write(output);
                }
            }
            System.out.println("文件加密成功: " + destinationPath);
        } catch (Exception e) {
            System.out.println("文件加密失败: " + e.getMessage());
        }
    }

    // 解密文件
    public void decryptFile(String sourcePath, String destinationPath) {
        try {
            SecretKey secretKey = decodeKeyFromString(this.secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (FileInputStream fis = new FileInputStream(sourcePath);
                 FileOutputStream fos = new FileOutputStream(destinationPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    byte[] output = cipher.update(buffer, 0, length);
                    if (output != null) {
                        fos.write(output);
                    }
                }
                byte[] output = cipher.doFinal();
                if (output != null) {
                    fos.write(output);
                }
            }
            System.out.println("文件解密成功: " + destinationPath);
        } catch (Exception e) {
            System.out.println("文件解密失败: " + e.getMessage());
        }
    }

    // 从字符串解码密钥
    private static SecretKey decodeKeyFromString(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    //压缩文件或文件夹 使用zip
//    public void compressFile() {
//        try {
//            long startTime = System.currentTimeMillis();
//
//            long endTime = System.currentTimeMillis();
//            System.out.println("压缩成功: " + zipPath);
//            System.out.println("压缩时间: " + (endTime - startTime) + " 毫秒");
//        } catch (IOException e) {
//            System.out.println("文件夹压缩失败: " + e.getMessage());
//        }
//    }
    // 压缩文件
    public void zipFile(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destinationFile))) {
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
                zos.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
            }
            System.out.println("文件压缩成功: " + destinationPath);
        } catch (IOException e) {
            System.out.println("文件压缩失败: " + e.getMessage());
        }
    }

    // 压缩文件夹
    public void zipDirectory(String sourcePath, String destinationPath)  {
        File sourceDir=new File(sourcePath);
        File destinationDir=new File(destinationPath);
        if (!destinationDir.exists()) {
            boolean created = destinationDir.getParentFile().mkdir();

        }
        try(ZipOutputStream zos =new ZipOutputStream(new FileOutputStream(destinationDir))){
            zipDirFile(sourceDir, sourceDir.getName(), zos);
            System.out.println("文件夹压缩成功: " + destinationPath);
        } catch (IOException e) {
            System.out.println("文件夹压缩失败: " + e.getMessage());
        }
    }
    //
    private static void zipDirFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipDirFile(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
        }
    }



    public void unzip(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
        //如果目标文件夹不存在，创建新的文件夹
        File destinationDir = new File(destinationPath);
        if (!destinationDir.exists()) {
            System.out.println("文件夹不存在，创建新的文件夹");
            boolean created = destinationDir.getParentFile().mkdir();
            System.out.println(created ? "文件夹创建成功" : "文件夹创建失败");
        }
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destinationPath + File.separator + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("创建文件失败：" + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("创建文件夹失败：" + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) >= 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            System.out.println("文件解压成功: " + destinationPath);
        } catch (IOException e) {
            System.out.println("文件解压失败: " + e.getMessage());
        }
    }


    // 关闭 ExecutorService，释放资源
    public void shutdown() {
        executorService.shutdown();
    }
//    public static void main(String args[]){
//        Path file = Path.of("E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\d1\\100.txt");
//        Path zipFile = Path.of("E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\d1\\100.zip");
//        try(ZipOutputStream zos =new ZipOutputStream(new FileOutputStream(String.valueOf(zipFile)))){
//            zipFile(file,zos);
//        } catch (IOException e) {
//            System.out.println("文件夹压缩失败: " + e.getMessage());
//        }
//
//
//    }


}
