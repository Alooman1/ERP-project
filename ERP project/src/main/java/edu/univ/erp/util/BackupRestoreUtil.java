package edu.univ.erp.util;

import java.io.File;
import java.io.IOException;

public class BackupRestoreUtil {

    // Path to MySQL program files on Windows
    private static final String MYSQL_BIN_PATH =
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\";

    // Database login details
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "Naveen23@";

    // Names of our two databases
    private static final String AUTH_DB = "auth_db";
    private static final String ERP_DB  = "erp_db";

    // Create backup files for both databases
    public static boolean backupDatabase(File baseFile) {

        // Create separate backup files for each database
        File authBackup = new File(baseFile.getAbsolutePath() + "_auth.sql");
        File erpBackup  = new File(baseFile.getAbsolutePath() + "_erp.sql");

        try {
            // Backup both databases
            boolean authOK = runBackup(AUTH_DB, authBackup);
            boolean erpOK  = runBackup(ERP_DB, erpBackup);
            return authOK && erpOK;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Run mysqldump command to backup a single database
    private static boolean runBackup(String dbName, File outputFile)
            throws IOException, InterruptedException {

        String dumpCmd = MYSQL_BIN_PATH + "mysqldump";

        // Build command to export database to SQL file
        ProcessBuilder pb = new ProcessBuilder(
                dumpCmd,
                "-u" + MYSQL_USER,
                "-p" + MYSQL_PASSWORD,
                "--databases", dbName,
                "--result-file=" + outputFile.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process p = pb.start();
        int status = p.waitFor();
        return status == 0;
    }

    // Restore database from SQL backup file
    public static boolean restoreDatabase(File sqlFile, String whichDB)
            throws IOException, InterruptedException {

        // Choose which database to restore
        String dbName = whichDB.equals("auth_db") ? AUTH_DB : ERP_DB;

        String mysqlCmd = MYSQL_BIN_PATH + "mysql";

        // Build command to import SQL file into database
        ProcessBuilder pb = new ProcessBuilder(
                mysqlCmd,
                "-u" + MYSQL_USER,
                "-p" + MYSQL_PASSWORD,
                dbName
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Feed the SQL file contents to mysql command
        java.nio.file.Files.copy(
                sqlFile.toPath(),
                process.getOutputStream()
        );

        process.getOutputStream().close();
        int status = process.waitFor();

        return status == 0;
    }
}