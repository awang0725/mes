package com.qcadoo.mes.beans.products;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "products_genealogy_product_in_batch")
public class ProductsGenealogyProductInBatch {

    @Id
    @GeneratedValue
    private Long id;

    private String batch;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductsGenealogyProductInComponent productInComponent;

    @Column(scale = 3, precision = 10)
    private BigDecimal quantity;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String worker;

    public Long getId() {
        return id;
    }

    public String getBatch() {
        return batch;
    }

    public ProductsGenealogyProductInComponent getProductInComponent() {
        return productInComponent;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setBatch(final String batch) {
        this.batch = batch;
    }

    public void setProductInComponent(final ProductsGenealogyProductInComponent productInComponent) {
        this.productInComponent = productInComponent;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(final String worker) {
        this.worker = worker;
    }

}
