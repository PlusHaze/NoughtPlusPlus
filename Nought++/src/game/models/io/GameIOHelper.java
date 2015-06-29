package game.models.io;

import game.models.logic.Player;
import game.models.misc.AlertBox;
import game.models.model.RecentScore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameIOHelper {

    public static final String APPLICATION_NAME = "NoughtPlusPlus";

    /**
     * Gets the working directory of which to the caller
     * @return The path to the folder to save the file on
     */
    private static Path getWorkingDirectory() {

        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (osName.contains("linux") || osName.contains("unix"))
            return new File(userHome, APPLICATION_NAME + "/").toPath();

        if (osName.contains("mac"))
            return new File(userHome, "Library/Application Support/" + APPLICATION_NAME).toPath();

        if (osName.contains("win")) {
            String applicationData = System.getenv("AppData");
            String folder = applicationData != null ? applicationData : userHome;

            return new File(folder, APPLICATION_NAME + "/").toPath();
        }

        return new File(userHome, APPLICATION_NAME + "/").toPath();
    }

    /**
     * Gets the full path of where the file is to be saved, if file doesn't exist then it's created
     * @return The full path of where the file is to be saved
     */
    private static Path getSavePath() {

        try {

            Path fullDirectory = getWorkingDirectory();
            Path fullPath = Paths.get(fullDirectory + "/RecentScores.npp");

            if (Files.notExists(fullDirectory))
                Files.createDirectory(fullDirectory);

            if (Files.notExists(fullPath))
                Files.createFile(fullPath);

            return fullPath;

        } catch (IOException e) {

            AlertBox.show("Error trying to read saved game data - Press okay to see error");
            AlertBox.show(e.getMessage());

            return null;
        }
    }

    /**
     * Loads game save data
     * @return A list of recent score objects from the file store
     * @throws IOException
     */
    public static List<RecentScore> loadRecentScores() throws IOException {

        Path path = getSavePath();

        if (path == null)
            return null;

        List<String> lines = Files.readAllLines(path);
        List<RecentScore> recentScoreList = new ArrayList<>();

        for (String line : lines) {

            Player p1, p2;
            String date;
            int gridSize;

            try {

                //Example data
                //Ben,0,Computer,1,3,2015/06/28 23:27:48

                String[] dataChunks = line.split(",");

                if (dataChunks.length != 6)
                    continue;

                p1 = new Player(dataChunks[0], null);
                p1.setScore(Integer.valueOf(dataChunks[1]));

                p2 = new Player(dataChunks[2], null);
                p2.setScore(Integer.valueOf(dataChunks[3]));

                gridSize = Integer.valueOf(dataChunks[4]);

                date = dataChunks[5];

            } catch (NumberFormatException e) {
                continue;
            }

            recentScoreList.add(new RecentScore(p1, p2, gridSize,date));
        }

        return recentScoreList;
    }

    /**
     * Saves a new recent score to a file
     * @param rs The recent score to append to file store
     */
    public static void saveGameScores(RecentScore rs) {

        Path saveLocation = getSavePath();

        if (saveLocation == null)
            return;

        try {

            StringBuilder sb = new StringBuilder();

            sb.append(rs.getPlayer1().getName()).append(",");
            sb.append(rs.getPlayer1().getScore()).append(",");

            sb.append(rs.getPlayer2().getName()).append(",");
            sb.append(rs.getPlayer2().getScore()).append(",");

            sb.append(rs.getAbsDimension()).append(",");

            sb.append(rs.getDate());

            sb.append(System.lineSeparator());

            //Append the data to existing file
            FileWriter fileWriter = new FileWriter(saveLocation.toString(),true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(sb.toString());
            bufferWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all of the saved files and folders associated with the game
     * @throws IOException
     */
    public static void deleteRecentScores() throws IOException {

        Path rootDirectory = Paths.get(getWorkingDirectory() + "/");
        File[] files = rootDirectory.toFile().listFiles();

        if (files != null)
            for (File file : files)
                Files.delete(file.toPath());

        Files.deleteIfExists(rootDirectory);
    }
}