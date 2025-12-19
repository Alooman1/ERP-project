package edu.univ.erp.service;

import edu.univ.erp.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserAuthDAO userAuthDAO;
    @Mock private StudentDAO studentDAO;
    @Mock private InstructorDAO instructorDAO;
    @Mock private AdminDAO adminDAO;
    @Mock private CourseDAO courseDAO;
    @Mock private EnrollmentDAO enrollmentDAO;
    @Mock private NotificationDAO notificationDAO;
    @Mock private GradeDAO gradeDAO;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userAuthDAO, studentDAO, instructorDAO, adminDAO, 
                                      courseDAO, enrollmentDAO, notificationDAO, gradeDAO);
    }

    @Test
    void testCreateNewStudent_Success() {
        // Arrange
        when(userAuthDAO.createUser(anyString(), anyString(), eq("Student"))).thenReturn(101);
        when(studentDAO.createStudentProfile(101, "New Student", "2025-999", "CS", 1)).thenReturn(true);

        // Act
        String result = adminService.createNewUser("Student", "stu99", "pass", "New Student", "2025-999", "CS", 1, null);

        // Assert
        assertTrue(result.startsWith("Success"));
        verify(userAuthDAO).createUser(eq("stu99"), anyString(), eq("Student"));
    }

    @Test
    void testSetMaintenanceMode() {
        // Arrange
        when(adminDAO.setMaintenanceMode(true)).thenReturn(true);

        // Act
        boolean result = adminService.setMaintenanceStatus(true);

        // Assert
        assertTrue(result);
        verify(adminDAO).setMaintenanceMode(true);
    }
    
    @Test
    void testCreateUser_MissingData_Fails() {
        // Act (Missing username)
        String result = adminService.createNewUser("Student", "", "pass", "Name", "Roll", "Prog", 1, null);
        
        // Assert
        assertEquals("Username, Password, and Full Name are required.", result);
        verifyNoInteractions(userAuthDAO);
    }
}