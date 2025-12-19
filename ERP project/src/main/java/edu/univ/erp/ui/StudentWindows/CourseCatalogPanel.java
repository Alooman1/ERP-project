package edu.univ.erp.ui.StudentWindows;

import edu.univ.erp.api.catalog.CatalogApi;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.UserSession;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class CourseCatalogPanel extends JPanel {

    private final UserSession session;
    private final CatalogApi catalogApi;

    private JTable courseTable;
    private CourseCatalogTableModel tableModel;

    public CourseCatalogPanel(UserSession session) {
        this.session = session;
        this.catalogApi = new CatalogApi();

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 40, 25, 40));

        // Title label at top
        JLabel titleLabel = new JLabel("COURSES");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Create empty table and apply styling
        tableModel = new CourseCatalogTableModel(List.of());
        courseTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(courseTable);
        styleScrollPane(scrollPane);

        // Reload data when this panel becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadCatalogData();
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Load data initially
        loadCatalogData();
    }

    // Apply dark theme styling to the table
    private void styleTable() {
        courseTable.setBackground(new Color(60, 60, 60));
        courseTable.setForeground(Color.WHITE);
        courseTable.setGridColor(new Color(80, 80, 80));
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        courseTable.setSelectionBackground(new Color(0, 120, 215));
        courseTable.setSelectionForeground(Color.WHITE);

        // Style the table header
        JTableHeader header = courseTable.getTableHeader();
        header.setBackground(new Color(45, 45, 45));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(100, 40));
    }

    // Style the scroll pane
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    // Load course data from server
    private void loadCatalogData() {
        SwingWorker<List<CatalogItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CatalogItem> doInBackground() throws Exception {
                // Get all available courses
                return catalogApi.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    List<CatalogItem> items = get();
                    // Update table with new data
                    tableModel = new CourseCatalogTableModel(items);
                    courseTable.setModel(tableModel);

                    // Set column widths for better display
                    courseTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Code
                    courseTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Title
                    courseTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Credits

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}