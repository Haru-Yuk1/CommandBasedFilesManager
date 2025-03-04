package service;

import da.FileRepository;

import java.io.File;
import java.nio.file.Path;

import static da.SystemData.getCurrentWorkingDirectory;
import static da.SystemData.getFileRepository;


/*
* FileService 是负责文件相关操作的服务类，包含了文件的基本增删改查功能。
* */
public class FileService {

    // 创建文件
    public void createFile(String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().createFile(String.valueOf(targetPath));
    }

    // 删除文件
    public void deleteFile(String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().deleteFile(String.valueOf(targetPath));
    }

    // 读取文件内容
    public void readFile(String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径

        getFileRepository().readFileContent(targetPath);
    }
    // 写入文件内容
    public void writeFile(String content,String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().writeFile(content, targetPath);
    }

    // 重命名文件
    public void renameFile(String oldName, String newName) {
        String directoryPath=getCurrentWorkingDirectory();   //目标路径

        String oldFullPath = directoryPath + File.separator + oldName;
        String newFullPath = directoryPath + File.separator + newName;
        getFileRepository().renameFile(oldFullPath, newFullPath);
    }

    // 复制文件
    public void copyFile(String sourceFilePath, String destinationFilePath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceFilePath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationFilePath);   //目标路径
        getFileRepository().copyFile(oldPath, newPath,false);
    }

    public void copyFile(String sourceFilePath, String destinationFilePath,String async) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceFilePath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationFilePath);   //目标路径
        if(async.equals("async")){
            getFileRepository().copyFile(oldPath, newPath, true);
        }
        else{
            if(async.equals("sync")){
                getFileRepository().copyFile(oldPath, newPath,false);
            }
            else{
                System.out.println("参数错误");
                return;
            }
        }

    }

    // 移动文件
    public void moveFile(String sourceFilePath, String destinationPath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceFilePath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationPath);   //目标路径
        getFileRepository().moveFile(oldPath, newPath);
    }

    // 压缩文件
    public void zipFile(String sourceFilePath, String destinationFilePath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceFilePath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationFilePath);   //目标路径
        getFileRepository().zipFile(String.valueOf(oldPath), String.valueOf(newPath));
    }

    // 加密文件
    public void encryptFile(String sourceFilePath, String destinationFilePath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceFilePath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationFilePath);   //目标路径
        getFileRepository().encryptFile(String.valueOf(oldPath), String.valueOf(newPath));
    }
    // 解密文件
    public void decryptFile(String sourceFilePath, String destinationFilePath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceFilePath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationFilePath);   //目标路径
        getFileRepository().decryptFile(String.valueOf(oldPath), String.valueOf(newPath));
    }
    public void shutdown(){
        getFileRepository().shutdown();
    }
}
