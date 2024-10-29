import service.FileService;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static utils.KeyUtils.generateSecretKey;

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

        //文件加密操作测试
        String sourceFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1.txt";
        String destinationFilePath = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1_encrypted.txt";
        String key="jMNFutwOiCJO4GqWIWBCPIp9NcvSNghzYXhwPJednRY=";
        encryptFile(key,sourceFilePath, destinationFilePath);

        //文件解密操作测试
        String sourceFilePath2 = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1_encrypted.txt";
        String destinationFilePath2 = "E:\\Work\\Project\\IDEAProject\\CommandBasedFilesManager\\workspace\\1_decrypted.txt";
        String key2="jMNFutwOiCJO4GqWIWBCPIp9NcvSNghzYXhwPJednRY=";
        decryptFile(key2,sourceFilePath2, destinationFilePath2);
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
