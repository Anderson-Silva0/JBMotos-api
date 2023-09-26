package com.example.jbmotos.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jbmotos.model.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

}
