package edu.univ.erp.ui.AdminWindows;

import edu.univ.erp.domain.UserSession;
import edu.univ.erp.util.BackupRestoreUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class AdminBackupRestorePanel extends JPanel {

    // Main constructor - sets up backup and restore panel
    public AdminBackupRestorePanel(UserSession session) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Main title
        JLabel titleLabel = new JLabel("BACKUP & RESTORE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);

        // Backup database button
        JButton backupButton = createButton("Backup Database");
        backupButton.addActionListener(e -> handleBackup());
        gbc.gridy = 0;
        mainPanel.add(backupButton, gbc);

        // Restore auth database button
        JButton restoreAuthButton = createButton("Restore Auth DB");
        restoreAuthButton.addActionListener(e -> handleRestore("auth_db"));
        gbc.gridy = 1;
        mainPanel.add(restoreAuthButton, gbc);

        // Restore ERP database button
        JButton restoreErpButton = createButton("Restore ERP DB");
        restoreErpButton.addActionListener(e -> handleRestore("erp_db"));
        gbc.gridy = 2;
        mainPanel.add(restoreErpButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    // Helper to create styled buttons
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setOpaque(true);
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));
        return button;
    }

    // Handles backing up the database to SQL files
    private void handleBackup() {
        // Let user choose where to save backup
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Backup As...");
        fc.setSelectedFile(new File("erp_backup.sql"));
        
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fc.getSelectedFile();
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return BackupRestoreUtil.backupDatabase(fileToSave);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(AdminBackupRestorePanel.this, 
                                "Backup successful!\nFiles saved to " + fileToSave.getAbsolutePath() + "_auth.sql and _erp.sql",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new Exception("Backup command failed.");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AdminBackupRestorePanel.this, 
                            "Error during backup: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    // Handles restoring database from SQL file
    private void handleRestore(String dbName) {
        // Let user choose SQL file to restore from
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select SQL file to restore " + dbName);
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToRestore = fc.getSelectedFile();
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return BackupRestoreUtil.restoreDatabase(fileToRestore, dbName);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(AdminBackupRestorePanel.this, 
                                dbName + " restored successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new Exception("Restore command failed.");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AdminBackupRestorePanel.this, 
                            "Error during restore: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}