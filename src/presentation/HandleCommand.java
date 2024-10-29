package presentation;

import service.DirectoryService;
import service.FileService;

import java.nio.file.Path;
import java.util.Scanner;



public class HandleCommand {
    public static void handleChangeDirectory(DirectoryService directoryService,String arg1) {
        // 调用 DirectoryService 的 setCurrentWorkingDirectory 方法
        directoryService.setCurrentWorkingDirectory(arg1);

    }
    public static void handleMkdir(DirectoryService directoryService,String arg1) {
        // 调用 DirectoryService 的 createDirectory 方法
        directoryService.createDirectory(arg1);
    }
    public static void handleRmdir(DirectoryService directoryService,String arg1) {
        // 调用 DirectoryService 的 deleteDirectory 方法
        directoryService.deleteDirectory(arg1);
    }
    public static void handleChdir(DirectoryService directoryService){
        System.out.println("当前路径：");
        System.out.println(directoryService.getWorkingDirectory());
    }

    // 处理dir命令 没有参数
    public static void handleDir(DirectoryService directoryService,String ...args){
        if (args.length == 0){
            System.out.println("目录内容：");
            directoryService.listCurrentDirectoryContents();
            return;
        }
        if (args.length == 1){
            System.out.println("目录内容：");
            if(args[0].contains("\\")){
                directoryService.listCurrentDirectoryContents(args[0]);
                return;
            }
            else{
                //如果参数中有通配符，
                if(args[0].contains("*")||args[0].contains("?")){
                    directoryService.listDirectoryContentsByPattern(args[0]);
                    return;
                }

                directoryService.listDirectoryContents(args[0]);
                return;
            }

        }
        if (args.length == 2){
            System.out.println("目录内容：");
            if(args[1].contains("*")||args[1].contains("?")){
                directoryService.listDirectoryContentsByPattern(args[0],args[1]);
                return;
            }
            directoryService.listDirectoryContents(args[0],args[1]);
            return;
        }

        System.out.println("目录内容：");
        directoryService.listCurrentDirectoryContents();
    }

    public static void handleDirByTime(DirectoryService directoryService,String arg1,String arg2){
        arg1=arg1.replaceFirst("-", "  ");
        arg2=arg2.replaceFirst("-", "  ");
        System.out.println("目录内容：");
        directoryService.listDirectoryContentsByTime(arg1,arg2);
    }

    public static void handleDirBySize(DirectoryService directoryService,String arg1,String arg2){
        System.out.println("目录内容：");
        directoryService.listDirectoryContentsBySize(arg1,arg2);
    }




    public static void handleCopyDir(DirectoryService directoryService,String arg1,String arg2){
        // 调用 DirectoryService 的 copyDirectory 方法
        directoryService.copyDirectory(arg1,arg2);
    }

    // 文件操作
    public static void handleCreateFile(FileService fileService,String arg1){
        // 调用 FileService 的 createFile 方法
        fileService.createFile(arg1);
    }
    public static void handleEcho(FileService fileService,String arg1,String arg2){
        // 调用 FileService 的 writeFile 方法
        fileService.writeFile(arg1,arg2);
    }

    public static void handleType(FileService fileService,String arg1){
        // 调用 FileService 的 readFile 方法
        fileService.readFile(arg1);
    }

    public static void handleDeleteFile(FileService fileService,String arg1){
        // 调用 FileService 的 deleteFile 方法
        fileService.deleteFile(arg1);
    }

    public static void handleRenameFile(FileService fileService,String arg1,String arg2){
        // 调用 FileService 的 renameFile 方法
        fileService.renameFile(arg1,arg2);
    }
    public static void handleCopyFile(FileService fileService,String arg1,String arg2){

        // 调用 FileService 的 copyFile 方法
        fileService.copyFile(arg1,arg2);
    }
    public static void handleMoveFile(FileService fileService,String arg1,String arg2){
        // 调用 FileService 的 moveFile 方法
        fileService.moveFile(arg1,arg2);
    }



    //加密操作
    public static void handleEncrypt(FileService fileService,String arg1,String arg2){
        fileService.encryptFile(arg1,arg2);
    }

    //解密操作
    public static void handleDecrypt(FileService fileService,String arg1,String arg2){
        fileService.decryptFile(arg1,arg2);
    }

    //压缩操作
    public static void handleZip(DirectoryService directoryService,String arg1,String arg2){
        directoryService.zipDirectory(arg1,arg2);
    }

    //解压操作
    public static void handleUnzip(DirectoryService directoryService,String arg1,String arg2){
        if (arg2.isEmpty()){
            arg2=directoryService.getWorkingDirectory();
        }
        directoryService.unzipDirectory(arg1,arg2);
    }


}
