package com.hsttcoder.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.hsttcoder.dscatalog.dto.ProductDTO;
import com.hsttcoder.dscatalog.entities.Category;
import com.hsttcoder.dscatalog.entities.Product;
import com.hsttcoder.dscatalog.repositories.CategoryRepository;
import com.hsttcoder.dscatalog.repositories.ProductRepository;
import com.hsttcoder.dscatalog.services.exceptions.DbException;
import com.hsttcoder.dscatalog.services.exceptions.ResourceNotFoundException;
import com.hsttcoder.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	private ProductDTO productDto;

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 9999L;
		dependentId = 4L;
		product = Factory.createProduct();
		category = Factory.createCategory();
		productDto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));

		// get one simulation
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);
		Mockito.doThrow(EntityNotFoundException.class).when(categoryRepository).getOne(nonExistingId);
		
		// save simulation
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		// find all simulation
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

		// find by id
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		// delete by id simulation
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	
	@Test
	public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDto);
		});
		
		Mockito.verify(repository).getOne(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		productDto = service.update(existingId, productDto);
		
		Assertions.assertNotNull(productDto);
		Mockito.verify(repository).getOne(existingId);
		Mockito.verify(categoryRepository).getOne(existingId);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository).findById(nonExistingId);
	}

	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO productDto = service.findById(existingId);

		Assertions.assertNotNull(productDto);
		Mockito.verify(repository).findById(existingId);
	}

	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> result = service.findAllPaged(pageable);

		Assertions.assertNotNull(result);

		Mockito.verify(repository).findAll(pageable);
	}

	@Test
	public void deleteShouldThrowDbExceptionWhenDepedentId() {

		Assertions.assertThrows(DbException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

}
