package com.zavyalov;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    private static final String GAME_NAME = "FF8";
    private static final String ZIP_SAVES = "Packed_saves.zip";

    public static void main(String[] args) {
        String gameDirectory = "D://Games/savegames";
        String saveGameDirectory = gameDirectory + "/" + GAME_NAME;
        List<GameProgress> gameProgress = Arrays.asList(
                new GameProgress(80, 34, 5, 505.6),
                new GameProgress(5, 17, 8, 612.1),
                new GameProgress(99, 72, 9, 1103.8)
        );

        List<String> saveDirectories = new ArrayList<>();
        File dir = new File(gameDirectory, GAME_NAME);
        if (dir.mkdir()) {
            System.out.println("Папка создана:  " + gameDirectory + "/" + GAME_NAME);
        } else {
            System.out.println("!!! Папка не была создана !!!  " + gameDirectory + "/" + GAME_NAME);
        }
        for (int i = 0; i < gameProgress.size(); i++) {
            saveGame(saveGameDirectory + "/save" + (i + 1) + ".dat", gameProgress.get(i));
            saveDirectories.add(i, saveGameDirectory + "/save" + (i + 1) + ".dat");
        }
        boolean checkZipWrite = zipFiles(saveGameDirectory + "/" + ZIP_SAVES, saveDirectories);
        delOldSaves(checkZipWrite, saveDirectories);
    }

    private static void saveGame(String directory, GameProgress progress) {
        try (FileOutputStream fos = new FileOutputStream(directory);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(progress);
            System.out.println("Файл записан: " + directory);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static boolean zipFiles(String zipDirectory, List<String> saveDirectories) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipDirectory))) {
            boolean successZip = false;
            for (int i = 0; i < saveDirectories.size(); i++) {
                try (FileInputStream fis = new FileInputStream(saveDirectories.get(i))) {
                    ZipEntry entry = new ZipEntry("packed_save" + (i + 1) + ".dat");
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                    successZip = true;
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    successZip = false;
                }
            }
            return successZip;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    private static void delOldSaves(boolean checkZipWrite, List<String> saveDirectories) {
        if (checkZipWrite == true) {
            System.out.println("Архивация сохранений успешно.");
            for (String directory : saveDirectories) {
                File newDir = new File(directory);
                if (newDir.delete()) {
                    System.out.println("Старый файл удален:  " + directory);
                } else {
                    System.out.println("!!! Не удалось удалить файл:  " + directory);
                }
            }
        } else {
            System.out.println("!!! Архивация сохранений не удалась.");
        }
    }
}
