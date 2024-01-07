package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.MyOder;

public interface MyOrderRepo extends JpaRepository<MyOder, Long> {

}
