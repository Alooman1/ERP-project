package edu.univ.erp.service;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentDAO studentDAO;
    @Mock private CourseDAO courseDAO;
    @Mock private EnrollmentDAO enrollmentDAO;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        // Inject mocks into the service using the constructor we added
        studentService = new StudentService(studentDAO, courseDAO, enrollmentDAO);
    }

    @Test
    void testGetStudentProfile_Found() {
        // Arrange
        Student mockStudent = new Student(1, "2025-001", "John Doe", "CS", 1, "john");
        when(studentDAO.getStudentProfile(1)).thenReturn(mockStudent);

        // Act
        Student result = studentService.getStudentProfile(1);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void testUpdateStudentName_Success() {
        // Arrange
        when(studentDAO.updateStudentName(1, "Jane Doe")).thenReturn(true);

        // Act
        boolean result = studentService.updateStudentName(1, "Jane Doe");

        // Assert
        assertTrue(result);
        verify(studentDAO).updateStudentName(1, "Jane Doe");
    }

    @Test
    void testUpdateStudentName_EmptyName_Fails() {
        // Act
        boolean result = studentService.updateStudentName(1, "");

        // Assert
        assertFalse(result); // Should fail validation in Service
        verifyNoInteractions(studentDAO); // DAO should never be called
    }
}