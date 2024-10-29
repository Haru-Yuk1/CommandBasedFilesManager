package presentation;

import da.FileRepository;
import da.SystemData;
import service.DirectoryService;
import service.FileService;

import java.util.Scanner;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import static presentation.HandleCommand.*;
import static utils.CommandLineFont.ANSI_BLUE;
import static utils.CommandLineFont.ANSI_RESET;
import static utils.SimpleUtils.pressEnterToContinue;

public class MainMenu extends SystemData {

    private static DirectoryService directoryService = getDirectoryService();
    private static FileService fileService = getFileService();


    public static void show() {
        welcome();
        showMainMenu();
    }

    // 欢迎
    public static void welcome() {

        String[] welcome = {
                " W     W  EEEEE  L      CCCCC  OOOOO  M     M  EEEEE ",
                " W     W  E      L     C      O     O M     M  E     ",
                " W  W  W  EEEE   L     C      O     O M  M  M  EEEE  ",
                "  W W W   E      L     C      O     O M M M M  E     ",
                "   W W    EEEEE  LLLLL  CCCCC  OOOOO  M     M  EEEEE "
        };
        System.out.println(ANSI_BLUE + "-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        for (String line : welcome) {
            System.out.println(line);
        }
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------" + ANSI_RESET);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(ANSI_BLUE + "-------------------------------");
        System.out.println("正在进入系统...");
        System.out.println("-------------------------------" + ANSI_RESET);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 清空控制台
        System.out.println(new String(new char[50]).replace("\0", "\r\n"));
    }

    public static void title() {
        System.out.println("-------------------------------");
        System.out.println("\tx x 文 件 管 理 系 统");
        System.out.println("-------------------------------");

    }

    public static void showMainMenu() {
        while (true) {
            title();
            System.out.println("输入help查看命令列表");
            System.out.println("输入exit退出系统");
            System.out.println("-------------------------------");
            System.out.print(directoryService.getWorkingDirectory());
            System.out.println(">");


            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            while (input.isEmpty()) {
                input = scanner.nextLine();
            }
            // 将命令和参数分离
            String[] parts = input.split("\\s+", 4);
            String command = parts[0].toLowerCase();
            String arg1 = parts.length > 1 ? parts[1] : null;
            String arg2 = parts.length > 2 ? parts[2] : null;
            String arg3 = parts.length > 3 ? parts[3] : null;
            switch (command) {
                case "help":
                    showHelp();
                    break;
                case "cd":
                    if (arg1 != null) {
                        handleChangeDirectory(directoryService, arg1);
                    } else {
                        System.out.println("请指定目录路径");
                    }
                    break;
                case "chdir":
                    handleChdir(directoryService);
                    break;
                case "dir":
                    if (arg1 != null) {
                        if (arg2 == null) {
                            handleDir(directoryService, arg1);
                        } else {
                            handleDir(directoryService, arg1, arg2);
                        }
                    } else {
                        handleDir(directoryService);
                    }
                    break;
                case "dirbytime":
                    if (arg1 != null && arg2 != null) {
                        handleDirByTime(directoryService, arg1, arg2);
                    } else {
                        System.out.println("请指定时间范围");
                    }
                    break;
                case "dirbysize":
                    if (arg1 != null && arg2 != null) {
                        handleDirBySize(directoryService, arg1, arg2);
                    } else {
                        System.out.println("请指定文件大小范围");

                    }
                    break;
                case "mkdir":
                    if (arg1 != null) {
                        handleMkdir(directoryService, arg1);
                    } else {
                        System.out.println("请指定目录路径");
                    }
                    break;
                case "rmdir":
                    if (arg1 != null) {
                        handleRmdir(directoryService, arg1);
                    } else {
                        System.out.println("请指定目录路径");
                    }
                    break;
                case "touch":
                    if (arg1 != null) {
                        handleCreateFile(fileService, arg1);
                    } else {
                        System.out.println("请指定文件路径");
                    }
                    break;
                case "echo":
                    if (arg1 != null && arg2 != null) {
                        handleEcho(fileService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定文件内容");
                    } else {
                        System.out.println("请指定文件路径");
                    }
                    break;
                case "type":
                    if (arg1 != null) {
                        handleType(fileService, arg1);
                    } else {
                        System.out.println("请指定文件路径");
                    }
                    break;

                case "del":
                    if (arg1 != null) {
                        handleDeleteFile(fileService, arg1);
                    } else {
                        System.out.println("请指定文件路径");
                    }
                    break;
                case "ren":
                    if (arg1 != null && arg2 != null) {
                        handleRenameFile(fileService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定原文件名称");
                    } else {
                        System.out.println("请指定新文件名称");
                    }
                    break;
                case "copy":
                    if (arg1 != null && arg2 != null) {
                        if(arg3!=null) {
                            if (getDirectoryService().isDirectory(arg1)) {
                                handleCopyDir(directoryService, arg1, arg2, arg3);
                            } else {
                                handleCopyFile(fileService, arg1, arg2, arg3);
                            }
                        }
                        else {
                            if (getDirectoryService().isDirectory(arg1)) {
                                handleCopyDir(directoryService, arg1, arg2);
                            } else {
                                handleCopyFile(fileService, arg1, arg2);
                            }
                        }

                    } else if (arg1 == null) {
                        System.out.println("请指定源文件路径");
                    } else {
                        System.out.println("请指定目标文件路径");
                    }
                    break;
                case "move":
                    if (arg1 != null && arg2 != null) {
                        handleMoveFile(fileService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定源文件路径");
                    } else {
                        System.out.println("请指定目标文件路径");
                    }
                    break;

                case "zip":
                    if (arg1 != null && arg2 != null) {
                        handleZip(directoryService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定源文件路径");
                    } else {
                        System.out.println("请指定目标文件路径");
                    }
                    break;

                case "unzip":
                    if (arg1 != null && arg2 != null) {
                        handleUnzip(directoryService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定源文件路径");
                    } else {
                        handleUnzip(directoryService, arg1, "");
                    }
                    break;
                case "encrypt":
                    if (arg1 != null && arg2 != null) {
                        handleEncrypt(fileService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定源文件路径");
                    } else {
                        System.out.println("请指定目标文件路径");
                    }
                    break;
                case "decrypt":
                    if (arg1 != null && arg2 != null) {
                        handleDecrypt(fileService, arg1, arg2);
                    } else if (arg1 == null) {
                        System.out.println("请指定源文件路径");
                    } else {
                        System.out.println("请指定目标文件路径");
                    }
                    break;
                case "exit":
                    showExitSystem();
                    System.exit(0);
                    break;
                default:
                    System.out.println("无效命令，请重新输入！");
                    showMainMenu();
            }
        }
    }


    //目录操作


    public static void showHelp() {
        System.out.println("-------------------------------");
        System.out.println("命令列表：");

        //目录操作
        System.out.println("dir\t\t\t\t查看目录内容");
        System.out.println("chdir\t\t\t查看当前路径");
        System.out.println("cd <directory_path>\t\t切换目录");
        System.out.println("mkdir <directory_name>\t创建目录");
        System.out.println("rmdir<directory_name>\t删除目录");

        //文件操作
        System.out.println("touch <file_name>\t\t创建文件");
        System.out.println("echo <content> <file_name>\t\t写入文件");
        System.out.println("type <file_name>\t\t查看文件内容");
        System.out.println("del <file_name>\t\t\t删除文件");
        System.out.println("ren <old_file_name> <new_file_name>\t重命名文件");
        System.out.println("copy <source_file> <destination_file>\t复制文件");
        System.out.println("move <source_file> <destination_file>\t移动文件");

        System.out.println("help\t\t显示命令列表");
        System.out.println("exit\t\t退出系统");
        System.out.println("-------------------------------");
        pressEnterToContinue();
    }

    //退出系统
    public static void showExitSystem() {
        System.out.println("-------------------------------");
        System.out.println("正在退出系统...");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-------------------------------");
        System.out.println("-------------------------------");
        System.out.println("感谢您的使用！");
        System.out.println("-------------------------------");

        String[] thanks = {
                " TTTTT  H   H   A   N   N  K   K  SSSS ",
                "   T    H   H  A A  NN  N  K  K  S     ",
                "   T    HHHHH AAAAA N N N  KKK    SSS  ",
                "   T    H   H A   A N  NN  K  K      S ",
                "   T    H   H A   A N   N  K   K  SSSS "
        };

        System.out.println(ANSI_BLUE + "-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        for (String line : thanks) {
            System.out.println(line);
        }
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------" + ANSI_RESET);
    }

}
