package da;

import service.DirectoryService;
import service.FileService;

public class SystemData {
    private static String currentWorkingDirectory = System.getProperty("user.dir") + "\\workspace";
    private static FileRepository fileRepository = new FileRepository();
    private static DirectoryService directoryService = new DirectoryService();
    private static FileService fileService = new FileService();

    public SystemData() {
    }

    public static String getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }

    public static void setCurrentWorkingDirectory(String currentWorkingDirectory) {
        SystemData.currentWorkingDirectory = currentWorkingDirectory;
    }

    public static FileRepository getFileRepository() {
        return fileRepository;
    }

    public static void setFileRepository(FileRepository fileRepository) {
        SystemData.fileRepository = fileRepository;
    }

    public static DirectoryService getDirectoryService() {
        return directoryService;
    }

    public static void setDirectoryService(DirectoryService directoryService) {
        SystemData.directoryService = directoryService;
    }

    public static FileService getFileService() {
        return fileService;
    }

    public static void setFileService(FileService fileService) {
        SystemData.fileService = fileService;
    }
}
