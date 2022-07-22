package com.hsttcoder.dscatalog.tests;

import java.time.Instant;

import com.hsttcoder.dscatalog.dto.ProductDTO;
import com.hsttcoder.dscatalog.entities.Category;
import com.hsttcoder.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product p = new Product(1L, "Phone", "Good Phone", 800.0, "htpps://img.com/img.png",
				Instant.parse("2020-10-20T03:00:00Z"));
		p.getCategories().add(new Category(2L, "Eletronics"));

		return p;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
}
