package com.project.young.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.young.productservice.dto.ProductRequest;
import com.project.young.productservice.model.Product;
import com.project.young.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    private static final int MONGO_PORT = 27017;

    private static final String MONGO_URI_FORMAT = "mongodb://%s:%s/product";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Container
    private static final GenericContainer<?> mongo = new GenericContainer(DockerImageName.parse("mongo"))
            .withExposedPorts(MONGO_PORT)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        mongo.start();
        registry.add("spring.data.mongodb.uri", () ->
                String.format(MONGO_URI_FORMAT, mongo.getHost(), mongo.getMappedPort(MONGO_PORT)));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product p1 = Product.builder()
                .name("macbook pro14")
                .description("macbook pro 14inch model")
                .price(BigDecimal.valueOf(2000))
                .build();

        Product p2 = Product.builder()
                .name("macbook pro16")
                .description("macbook pro 16inch model")
                .price(BigDecimal.valueOf(3000))
                .build();

        productRepository.save(p1);
        productRepository.save(p2);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/product")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(2, productRepository.findAll().size());
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iphone 13")
                .description("iphone 13")
                .price(BigDecimal.valueOf(1300))
                .build();
    }

}
