package com.bd10.cloud.esfull.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product",type = "item",shards = 1,replicas = 0)
public class Item {

    public Item(){

    }
    
    public Item(Long id,String title,String brand,String category,String image,Double price){
        this.id=id;
        this.title=title;
        this.brand=brand;
        this.category=category;
        this.price=price;
        this.image=image;
    }
    /*
    * indexName：对应索引库的名称
    * type：对应在索引库中的类型
    * shards：分片数量，默认是5
    * replicas：副本数量，默认是1
    *
    * @Id：标记主键
    * @Field：指定映射属性
    * index：是否索引，默认为true
    * type：字段类型
    * store：是否存储，默认是false
    * analyzer：分词器类型
    * */
    @Id
    Long id;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    String title;//标题
    @Field(type = FieldType.Keyword)
    String category;//分类
    @Field(type = FieldType.Keyword)
    String brand;//品牌
    @Field(type = FieldType.Double)
    Double price;//价格
    @Field(index = false,type = FieldType.Keyword)
    String image;//图片地址

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                '}';
    }
}
