package com.hsttcoder.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsttcoder.dscatalog.dto.ProductDTO;
import com.hsttcoder.dscatalog.services.ProductService;
import com.hsttcoder.dscatalog.services.exceptions.DbException;
import com.hsttcoder.dscatalog.services.exceptions.ResourceNotFoundException;
import com.hsttcoder.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProductService service;

	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private long existingId;
	private long nonExistingId;
	private long dependentId;

	@BeforeEach
	void setUp() {

		existingId = 1L;
		nonExistingId = 9999L;
		dependentId = 4L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));

		// insert simulation
		when(service.insert(any())).thenReturn(productDTO);

		// find all paged simulation
		when(service.findAllPaged(any())).thenReturn(page);

		// find by id simulation
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		// update simulation
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		// delete simulation
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DbException.class).when(service).delete(dependentId);
	}


	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		mockMvc.perform(delete("/products/{id}", existingId)).andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
		mockMvc.perform(delete("/products/{id}", dependentId)).andExpect(status().isBadRequest());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		mockMvc.perform(delete("/products/{id}", nonExistingId)).andExpect(status().isNotFound());
	}

	@Test
	public void insertShouldReturnCreatedAndProductDTO() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
		mockMvc.perform(get("/products/{id}", existingId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdExists() throws Exception {
		mockMvc.perform(get("/products/{id}", nonExistingId)).andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products")).andExpect(status().isOk());
	}

}
