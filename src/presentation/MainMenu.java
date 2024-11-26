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

/*
* MainMenu 是文件管理系统的主菜单，负责展示系统的主界面和处理用户输入的命令。
*
* */

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
            System.out.printf(directoryService.getWorkingDirectory());
            System.out.print(">");


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
                        if(getDirectoryService().isDirectory(arg1)){
                            handleZip(directoryService, arg1, arg2);
                        }
                        else{
                            handleZip(fileService, arg1, arg2);
                        }
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
                    break;
            }
        }
    }

    //显示帮助
    public static void showHelp() {
        System.out.println("-------------------------------");
        System.out.println("命令列表：");

        //目录操作
        System.out.printf("%-40s%s\n", "dir", "查看当前目录内容");
        System.out.printf("%-40s%s\n", "dir <directory_path>", "查看指定目录内容");
        System.out.printf("%-40s%s\n", "dir <command> <directory_path>", "根据命令查看目录内容");
        System.out.printf("%-40s%s\n", "dir \\t", "按时间排序目录内容");
        System.out.printf("%-40s%s\n", "dir \\s", "按大小排序目录内容");
        System.out.printf("%-40s%s\n", "dir \\n", "按文件名排序目录内容");
        System.out.printf("%-40s%s\n", "dir *\\? ", "使用通配符过滤目录内容");
        System.out.printf("%-40s%s\n", "dirbytime <start_time> <end_time>", "按时间过滤目录内容");
        System.out.printf("%-40s%s\n", "dirbysize <min_size> <max_size>", "按大小过滤目录内容");

        System.out.printf("%-40s%s\n", "chdir", "查看当前路径");
        System.out.printf("%-40s%s\n", "cd <directory_path>", "切换目录");
        System.out.printf("%-40s%s\n", "mkdir <directory_name>", "创建目录");
        System.out.printf("%-40s%s\n", "rmdir<directory_name>", "删除目录");

        //文件操作
        System.out.printf("%-40s%s\n", "touch <file_name>", "创建文件");
        System.out.printf("%-40s%s\n", "echo <content> <file_name>", "写入文件");
        System.out.printf("%-40s%s\n", "type <file_name>", "查看文件内容");
        System.out.printf("%-40s%s\n", "del <file_name>", "删除文件");
        System.out.printf("%-40s%s\n", "ren <old_file_name> <new_file_name>", "重命名文件");
        System.out.printf("%-40s%s\n", "copy <source_file> <destination_file>", "复制文件");
        System.out.printf("%-40s%s\n", "move <source_file> <destination_file>", "移动文件");

        //压缩解压操作
        System.out.printf("%-40s%s\n", "zip <source_file> <destination_file>", "压缩文件");
        System.out.printf("%-40s%s\n", "unzip <source_file> <destination_file>", "解压文件");

        //加密解密操作
        System.out.printf("%-40s%s\n", "encrypt <source_file> <destination_file>", "加密文件");
        System.out.printf("%-40s%s\n", "decrypt <source_file> <destination_file>", "解密文件");

        System.out.printf("%-40s%s\n", "help", "显示命令列表");
        System.out.printf("%-40s%s\n", "exit", "退出系统");
        System.out.println("-------------------------------");
        pressEnterToContinue();
    }

    //退出系统
    public static void showExitSystem() {
        System.out.println("-------------------------------");
        System.out.println("正在退出系统...");
        fileService.shutdown();
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
