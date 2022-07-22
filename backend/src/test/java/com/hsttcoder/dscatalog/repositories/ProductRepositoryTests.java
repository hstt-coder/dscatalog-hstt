package com.hsttcoder.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.hsttcoder.dscatalog.entities.Product;
import com.hsttcoder.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts = 25L;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 20000L;
	}

	
	@Test
	public void findByIdShouldReturnProductWhenIdExist() {
		Optional<Product> result = repository.findById(existingId);
		
		Assertions.assertTrue(result.isPresent());
	}
	
	
	@Test
	public void findByIdShouldReturnNullWhenIdExist() {
		Optional<Product> result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Product p = Factory.createProduct();
		p.setId(null);
		
		p = repository.save(p);
		
		Assertions.assertNotNull(p.getId());
		Assertions.assertEquals(countTotalProducts + 1, p.getId());
		
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId);

		Optional<Product> result = repository.findById(existingId);

		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {

			repository.deleteById(nonExistingId);
		});
	}

}
