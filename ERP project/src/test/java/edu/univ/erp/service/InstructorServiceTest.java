package edu.univ.erp.service;

import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.domain.AssignedSection;
import edu.univ.erp.domain.Instructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock private InstructorDAO instructorDAO;
    private InstructorService instructorService;

    @BeforeEach
    void setUp() {
        instructorService = new InstructorService(instructorDAO);
    }

    @Test
    void testGetInstructorProfile() {
        Instructor mockInst = new Instructor(2, "Dr. Smith", "CS", "inst1");
        when(instructorDAO.getInstructorProfile(2)).thenReturn(mockInst);

        Instructor result = instructorService.getInstructorProfile(2);
        assertEquals("Dr. Smith", result.getFullName());
    }

    @Test
    void testGetAssignedSections() {
        AssignedSection sec1 = new AssignedSection(10, "CS101", "Intro", "Mon 10:00", "C1");
        when(instructorDAO.getAssignedSections(2)).thenReturn(Arrays.asList(sec1));

        List<AssignedSection> result = instructorService.getAssignedSections(2);
        assertFalse(result.isEmpty());
        assertEquals("CS101", result.get(0).getCourseCode());
    }

    @Test
    void testUpdateInstructorName_Null_Fails() {
        boolean result = instructorService.updateInstructorName(2, null);
        assertFalse(result);
        verifyNoInteractions(instructorDAO);
    }
}