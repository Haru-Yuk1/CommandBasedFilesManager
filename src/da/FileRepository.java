package da;


import utils.SimpleUtils;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
*FileRepository 提供底层文件系统操作的实现，直接与文件系统交互，
* 包括判断路径是否合法、创建文件夹、删除文件夹等。
*
* */
public class FileRepository {
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
            boolean deleted = directory.delete();
            System.out.println(deleted ? "文件夹删除成功" : "文件夹删除失败");
        } else {
            System.out.println("目录不存在");
        }
    }

    // 列出目录内容
    public void listFiles(String path) {
        File directory = new File(path);
        System.out.println("-".repeat(80));
        System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");

        File[] files = directory.listFiles();
        if (files != null) {
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
                    System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),  URLConnection.guessContentTypeFromName(file.getName()));
                }

            }
        } else {
            System.out.println("无法访问目录内容");
        }
    }
    // 列出目录内容
    public void listFiles(String command,String path) {
        File directory = new File(path);

        System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");

        File[] files = directory.listFiles();
        if (files != null) {
            //如果是-t命令,按时间排序
            if (command.contains("\\t")){
                System.out.println("按照时间排序");

                if(command.contains("r")){
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                }else {
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified));
                }

            }
            //如果是-s命令,按大小排序
            else if (command.contains("\\s")) {

                System.out.println("按照大小排序");
                Arrays.sort(files, Comparator.comparingLong(File::length));
            }
            //如果是-n命令,按文件名排序
            else if (Objects.equals(command, "\\n")) {
                System.out.println("按照文件名排序");
                Arrays.sort(files, Comparator.comparing(File::getName));
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
                    System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),  URLConnection.guessContentTypeFromName(file.getName()));
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
        System.out.printf("|%-29s|%-9s|%-19s|%-19s|%n", "最后修改时间", "大小", "文件名", "文件类型");
        File[] files = directory.listFiles();
        if (files != null) {
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
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),  URLConnection.guessContentTypeFromName(file.getName()));
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
            if (Objects.equals(command, "\\t")){
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            }
            //如果是-s命令,按大小排序
            else if (Objects.equals(command, "\\s")) {
                Arrays.sort(files, Comparator.comparingLong(File::length));
            }
            //如果是-n命令,按文件名排序
            else if (Objects.equals(command, "\\n")) {
                Arrays.sort(files, Comparator.comparing(File::getName));
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
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(),  URLConnection.guessContentTypeFromName(file.getName()));
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
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(), URLConnection.guessContentTypeFromName(file.getName()));
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
                        System.out.printf("|%-30s|%-10d|%-20s|%-20s|%n", formattedTime, file.length(), file.getName(), URLConnection.guessContentTypeFromName(file.getName()));
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

    // 复制文件
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
    // 拷贝文件夹（深度拷贝）：包括文件夹中的所有文件
    public void copyDirectory(Path sourcePath, Path destinationPath) {
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
    // 移动文件
    public void moveFile(Path sourcePath, Path destinationPath) {
        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("文件移动成功: " + destinationPath);
        } catch (IOException e) {
            System.out.println("文件移动失败: " + e.getMessage());
        }
    }



//    //压缩文件或文件夹 使用zip
//    public void compressFile(Path sourcePath, Path zipPath) {
//        try {
//            long startTime = System.currentTimeMillis();
//            try(ZipOutputStream zos =new ZipOutputStream(new FileOutputStream(String.valueOf(zipPath)))){
//                if(Files.isDirectory(sourcePath)){
//
//                }else {
//
//                }
//            }
//            long endTime = System.currentTimeMillis();
//            System.out.println("文件夹压缩成功: " + destinationPath);
//            System.out.println("压缩时间: " + (endTime - startTime) + " 毫秒");
//        } catch (IOException e) {
//            System.out.println("文件夹压缩失败: " + e.getMessage());
//        }
//    }
//
//    // 压缩文件夹
//    private void zipDirectory(Path sourceDir, String fileName, ZipOutputStream zos) throws IOException {
//        Files.walk(sourceDir).forEach(path -> {
//            try {
//                if (Files.isDirectory(path)) {
//                    // 如果是目录，则添加到 ZIP 中，不包括路径
//
//                    return;
//                }
//                //
//                zipFile(path, zos);
//            } catch (IOException e) {
//                System.out.println("压缩目录时出错: " + e.getMessage());
//            }
//        });
//    }
//    // 压缩单个文件
//    private void zipFile(Path file, ZipOutputStream zos) throws IOException {
//        try (InputStream is = Files.newInputStream(file)) {
//            ZipEntry zipEntry = new ZipEntry(file.toString());
//            zos.putNextEntry(zipEntry);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = is.read(buffer)) >= 0) {
//                zos.write(buffer, 0, length);
//            }
//            zos.closeEntry();
//        }
//    }

}
