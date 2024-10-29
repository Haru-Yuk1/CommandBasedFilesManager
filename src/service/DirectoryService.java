package service;

import da.FileRepository;
import da.SystemData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static da.SystemData.getCurrentWorkingDirectory;
import static da.SystemData.getFileRepository;


/*
* DirectoryService 是负责目录相关操作的服务类，包含设置当前工作文件夹以及文件夹的基本增删改查功能。
* 这个类直接面向 MainMenu，并调用 FileRepository 进行底层文件系统的操作。
* */
public class DirectoryService {

    public boolean isDirectory(String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        return getFileRepository().isDirectory(String.valueOf(targetPath));

    }

    // 设置当前工作目录 cd命令
    public void setCurrentWorkingDirectory(String path) {
        //获取当前工作目录
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        // resolve() 方法如果绝对路径则返回绝对路径，如果相对路径则返回相对路径
        if(!path.contains("..")){
            targetPath=targetPath.resolve(path);  // 解析相对路径
        }

        // 规范化路径，处理 ".."
        while (path.contains("..")) {  // 检查是否返回上一级目录
            targetPath =  targetPath.getParent(); // 获取上一级目录
            if (targetPath == null) {
                System.out.println("已经在根目录，无法返回上一级。");
                return;
            }
            path=path.replaceFirst("\\.\\.", "");
//            System.out.println(path);
//            System.out.println(targetPath);
        }

        System.out.println("目标路径："+targetPath);
        File newDir = new File(String.valueOf(targetPath));
//        //检查是否为绝对路径
//        if (!newDir.isAbsolute()) {
//            //将相对路径转换为绝对路径
//            newDir = new File(currentWorkingDirectory, path);
//        }
        if (getFileRepository().isDirectory(newDir.getAbsolutePath())) {
            SystemData.setCurrentWorkingDirectory(newDir.getAbsolutePath());
        } else {
            System.out.println("无效的目录路径: " + newDir.getAbsolutePath());
        }
    }
    // 获取当前工作目录 chdir命令
    public String getWorkingDirectory() {
        return getCurrentWorkingDirectory();
    }

    // 在当前工作目录下创建文件夹 mkdir命令
    public void createDirectory(String path) {

        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().createDirectory(String.valueOf(targetPath));
    }

    // 删除指定文件夹 rmdir命令
    public void deleteDirectory(String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().deleteDirectory(String.valueOf(targetPath));
    }

    // 列出当前目录的内容 dir命令
    public void listCurrentDirectoryContents() {
        getFileRepository().listFiles(getCurrentWorkingDirectory());
    }
    public void listCurrentDirectoryContents(String command) {
        getFileRepository().listFiles(command,getCurrentWorkingDirectory());
    }
    // 列出指定目录的内容 dir命令 有点神奇 dir ../..能执行
    public void listDirectoryContents(String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().listFiles(String.valueOf(targetPath));
    }
    public void listDirectoryContents(String command,String path) {
        Path targetPath=Path.of(getCurrentWorkingDirectory());   //目标路径
        targetPath=targetPath.resolve(path);  // 解析相对路径
        getFileRepository().listFiles(command,String.valueOf(targetPath));
    }
    // 列出指定目录的内容 dir命令 保证 通配符 * ?这种能执行
    public void listDirectoryContentsByPattern(String path) {
        //检查是否为绝对路径,这里不能用Path.of(path)因为path中有通配符
        File file = new File(path);
        if (!file.isAbsolute()) {
            path = new File(getCurrentWorkingDirectory(), path).getAbsolutePath();
        }
        getFileRepository().listFilesByPattern(path);

    }
    // 列出指定目录的内容 dir命令 保证 通配符 * ?这种能执行
    public void listDirectoryContentsByPattern(String command,String path) {
        //检查是否为绝对路径,这里不能用Path.of(path)因为path中有通配符
        File file = new File(path);
        if (!file.isAbsolute()) {
            path = new File(getCurrentWorkingDirectory(), path).getAbsolutePath();
        }
        getFileRepository().listFilesByPattern(command,path);

    }
    public void listDirectoryContentsByTime(String startTime,String endTime) {
        getFileRepository().listFilesByTime(getCurrentWorkingDirectory(),startTime,endTime);
    }
    public void listDirectoryContentsBySize(String minSize,String maxSize) {
        getFileRepository().listFilesBySize(getCurrentWorkingDirectory(),minSize,maxSize);
    }

    public void copyDirectory(String sourceDirectoryPath, String destinationDirectoryPath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceDirectoryPath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationDirectoryPath);   //目标路径
        getFileRepository().copyDirectory(oldPath, newPath);
    }


    // 压缩操作
    public void zipDirectory(String sourceDirectoryPath, String destinationDirectoryPath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceDirectoryPath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationDirectoryPath);   //目标路径
        getFileRepository().zipDirectory(String.valueOf(oldPath), String.valueOf(newPath));
    }
    public void unzipDirectory(String sourceDirectoryPath, String destinationDirectoryPath) {
        Path oldPath=Path.of(getCurrentWorkingDirectory()).resolve(sourceDirectoryPath);   //原路径
        Path newPath=Path.of(getCurrentWorkingDirectory()).resolve(destinationDirectoryPath);   //目标路径
        getFileRepository().unzip(String.valueOf(oldPath), String.valueOf(newPath));
    }
}
