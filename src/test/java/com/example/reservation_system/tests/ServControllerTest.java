package com.example.reservation_system.tests;

import com.example.reservation_system.controllers.ServController;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.services.ServService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Load the full application context
@AutoConfigureMockMvc // Enable MockMvc for testing MVC controllers
class ServControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServService servService;

    private Serv serv;

    @BeforeEach
    void setUp() {
        serv = new Serv();
        serv.setServiceId(1L);
        serv.setServiceName("Test Service");
    }

    @Test
    void testGetAllServices() {
        // Arrange
        List<Serv> services = Arrays.asList(serv, new Serv());
        when(servService.getAllServices()).thenReturn(services);

        // Act
        List<Serv> result = servService.getAllServices();

        // Assert
        assertEquals(2, result.size());
        verify(servService, times(1)).getAllServices();
    }

    @Test
    void testGetServiceById_found() {
        // Arrange
        when(servService.getServiceById(1L)).thenReturn(Optional.of(serv));

        // Act
        ResponseEntity<Serv> response = new ServController(servService).getServiceById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serv, response.getBody());
    }

    @Test
    void testGetServiceById_notFound() {
        // Arrange
        when(servService.getServiceById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Serv> response = new ServController(servService).getServiceById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateService_withoutAuthorization() throws Exception {
        // Attempt to create service as a USER role, expecting 403 Forbidden
        mockMvc.perform(post("/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serv)))
                .andExpect(status().isForbidden()); // Expecting 403 FORBIDDEN for unauthorized role

        verify(servService, never()).createService(any(Serv.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateService_withAuthorization() throws Exception {
        when(servService.createService(any(Serv.class))).thenReturn(serv);

        mockMvc.perform(post("/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serv)))
                .andExpect(status().isOk());

        verify(servService, times(1)).createService(any(Serv.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    void testUpdateService_found() {
        when(servService.getServiceById(1L)).thenReturn(Optional.of(serv));

        ResponseEntity<String> response = new ServController(servService).updateService(1L, serv);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Servis upraven úspěšně.", response.getBody());
        verify(servService, times(1)).updateService(1L, serv);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    void testUpdateService_notFound() {
        when(servService.getServiceById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = new ServController(servService).updateService(1L, serv);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(servService, never()).updateService(anyLong(), any(Serv.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteService_found() {
        when(servService.getServiceById(1L)).thenReturn(Optional.of(serv));

        ResponseEntity<String> response = new ServController(servService).deleteService(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Servis smazán úspěšně.", response.getBody());
        verify(servService, times(1)).deleteService(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteService_notFound() {
        when(servService.getServiceById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = new ServController(servService).deleteService(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(servService, never()).deleteService(anyLong());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteService_withoutAuthorization() throws Exception {
        mockMvc.perform(delete("/services/1"))
                .andExpect(status().isForbidden()); // Expecting 403 FORBIDDEN for unauthorized role

        verify(servService, never()).deleteService(anyLong());
    }
}
