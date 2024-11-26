package utils;


/*
* SimpleUtils 是一个工具类，包含一些简单的工具方法。
*
* */
public class SimpleUtils {
    //按Enter键继续
    public static void pressEnterToContinue() {
        System.out.println("请输入Enter继续...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示进度条
    public static void showProgressBar(long current, long total) {
        int percent = (int) ((current * 100) / total);
        int barLength = 50;
        int filledLength = (int) (barLength * percent / 100);

        StringBuilder progressBar = new StringBuilder();
        for(int i = 0; i < filledLength; i++) {
            progressBar.append("█");
        }
        for (int i = 0; i < barLength - filledLength; i++) {
            progressBar.append(" ");
        }

        System.out.print("\r进度: [" + progressBar + "] " + percent + "%");
        if(current == total) {
            System.out.println();
        }
    }
}
