// src/main/java/com/products/products/dto/ProductInvoiceDTO.java
package com.products.products.models;

import java.math.BigDecimal;

public record ProductInvoiceDTO(Long id, String name, BigDecimal price) {}
