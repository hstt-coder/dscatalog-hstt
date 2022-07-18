package com.hsttcoder.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsttcoder.dscatalog.entities.Category;
import com.hsttcoder.dscatalog.repositories.CategoryRepository;


@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	
	public List<Category> findAll() {
		return repository.findAll();
	}
}
