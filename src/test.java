import service.FileService;
import utils.SimpleUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static utils.KeyUtils.generateSecretKey;

public class test {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        // 文件拷贝操作测试
//        String sourceFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\test.zip";
//        String destinationFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\test2.zip";
//        copyFile(Path.of(sourceFilePath), Path.of(destinationFilePath), true);
//
//        String sourceFilePath2 = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\test.zip";
//        String destinationFilePath2 = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\test3.zip";
//        copyFile(Path.of(sourceFilePath2), Path.of(destinationFilePath2),false);

//        // 日期格式化操作
//        String time = "2024/09/13  19:36";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm");
//        LocalDate date = LocalDate.parse(time, formatter);
//        System.out.println(date);

        //文件压缩操作测试
//        String sourceFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1.txt";
//        String destinationFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1.zip";
//        zipDirectory(sourceFilePath, destinationFilePath);

        //文件解压操作测试
//        String sourceFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\d2.zip";
//        String destinationFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\d3";
//        unzip(sourceFilePath, destinationFilePath);

//        //文件加密操作测试
//        String sourceFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1.txt";
//        String destinationFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1_encrypted.txt";
//        String key="jMNFutwOiCJO4GqWIWBCPIp9NcvSNghzYXhwPJednRY=";
//        encryptFile(key,sourceFilePath, destinationFilePath);
//
//        //文件解密操作测试
//        String sourceFilePath2 = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1_encrypted.txt";
//        String destinationFilePath2 = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1_decrypted.txt";
//        String key2="jMNFutwOiCJO4GqWIWBCPIp9NcvSNghzYXhwPJednRY=";
//        decryptFile(key2,sourceFilePath2, destinationFilePath2);
//        Path path = Path.of("E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\*.txt");
    }

    //文件拷贝
    public static void copyFile(Path sourcePath, Path destinationPath) {

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
            System.out.println("文件复制成功: " + destinationPath);
            System.out.println("复制时间: " + (endTime - startTime) + " 毫秒");
        } catch (IOException e) {
            System.out.println("文件复制失败: " + e.getMessage());
        }
    }


    // 复制文件
    public static void copyFile(Path sourcePath, Path destinationPath, boolean async) {
        if (async) {
            executorService.submit(() -> {
                copyFile(sourcePath, destinationPath);
            });
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

    public static void zipDirectory(String sourcePath, String destinationPath) {
        File sourceDir = new File(sourcePath);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destinationPath))) {
            zipFile(sourceDir, sourceDir.getName(), zos);

            System.out.println("文件夹压缩成功: " + destinationPath);
        } catch (IOException e) {
            System.out.println("文件夹压缩失败: " + e.getMessage());
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
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
                    zipFile(childFile, fileName + "/" + childFile.getName(), zos);
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

    public static void unzip(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
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

    // 加密文件
    public static void encryptFile(String key, String sourcePath, String destinationPath) {
        try {
            SecretKey secretKey = decodeKeyFromString(key);
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
    public static void decryptFile(String key, String sourcePath, String destinationPath) {
        try {
            SecretKey secretKey = decodeKeyFromString(key);
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

}
