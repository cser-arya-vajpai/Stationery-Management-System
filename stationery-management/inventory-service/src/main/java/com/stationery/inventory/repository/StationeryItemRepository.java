package com.stationery.inventory.repository;

import com.stationery.inventory.model.Category;
import com.stationery.inventory.model.StationeryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository    //this is a data access bean
public interface StationeryItemRepository extends JpaRepository<StationeryItem, Long> {
    Page<StationeryItem> findAll(Pageable pageable);
    List<StationeryItem> findByCategory(Category category);
    List<StationeryItem> findByAvailableQuantityLessThan(Integer quantity);
}