package com.bd10.cloud.esfull.dao;

import com.bd10.cloud.esfull.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemDao extends ElasticsearchRepository<Item,Long> {
    List<Item> findByPriceBetween(double p1, double p2);

    List<Item> findByBrandAndPrice(String brand, double price);

    List<Item> findByBrandOrPrice(String brand, double price);

    List<Item> findByTitleLike(String title);
}
