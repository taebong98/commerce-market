package flab.commercemarket.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.controller.product.ProductController;
import flab.commercemarket.controller.product.dto.ProductDto;
import flab.commercemarket.controller.product.dto.ProductResponseDto;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    void postProductTest() throws Exception {
        ProductDto productDto = makeProductDtoFixture(1);
        Product product = productDto.toProduct();
        ProductResponseDto productResponseDto = product.toProductResponseDto();

        when(productService.registerProduct(any(Product.class))).thenReturn(product);


        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);
        ResultActions perform = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void patchProductTest() throws Exception {
        long id = 1L;

        ProductDto productDto = makeProductDtoFixture(1);
        Product product = productDto.toProduct();
        product.setId(id);
        ProductResponseDto productResponseDto = product.toProductResponseDto();

        when(productService.updateProduct(eq(id), any(Product.class))).thenReturn(product);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);

        ResultActions perform = mockMvc.perform(patch("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void getProductTest() throws Exception {
        long id = 1L;

        ProductDto productDto = makeProductDtoFixture(1);
        Product product = productDto.toProduct();
        product.setId(id);
        ProductResponseDto productResponseDto = product.toProductResponseDto();

        when(productService.findProduct(eq(id))).thenReturn(product);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);

        ResultActions perform = mockMvc.perform(get("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void getProductsTest() throws Exception {
        int page = 1;
        int size = 10;
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);

        List<Product> expectedProducts = Arrays.asList(
                product1, product2
        );

        when(productService.findProducts(page, size)).thenReturn(expectedProducts);
        when(productService.countGetProduct()).thenReturn(2);

        mockMvc.perform(get("/products")
                        .param("page",  String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(expectedProducts.size()))
                .andExpect(jsonPath("$.content[0].id").value(product1.getId()))
                .andExpect(jsonPath("$.content[0].name").value(product1.getName()))
                .andExpect(jsonPath("$.content[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$.content[0].imageUrl").value(product1.getImageUrl()))
                .andExpect(jsonPath("$.content[0].description").value(product1.getDescription()))
                .andExpect(jsonPath("$.content[0].stockAmount").value(product1.getStockAmount()))
                .andExpect(jsonPath("$.content[1].id").value(product2.getId()))
                .andExpect(jsonPath("$.content[1].name").value(product2.getName()))
                .andExpect(jsonPath("$.content[1].price").value(product2.getPrice()))
                .andExpect(jsonPath("$.content[1].imageUrl").value(product2.getImageUrl()))
                .andExpect(jsonPath("$.content[1].description").value(product2.getDescription()))
                .andExpect(jsonPath("$.content[1].stockAmount").value(product2.getStockAmount()))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(expectedProducts.size()));
    }

    @Test
    void searchProductTest() throws Exception {
        String keyword = "product";
        int size = 10;
        int page = 1;
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);

        List<Product> expectedProducts = Arrays.asList(product1, product2);

        when(productService.searchProduct(keyword, page, size)).thenReturn(expectedProducts);
        when(productService.countSearchProductByKeyword(keyword)).thenReturn(2);

        mockMvc.perform(get("/products/search")
                        .param("keyword", keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(expectedProducts.size()))
                .andExpect(jsonPath("$.content[0].id").value(product1.getId()))
                .andExpect(jsonPath("$.content[0].name").value(product1.getName()))
                .andExpect(jsonPath("$.content[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$.content[0].imageUrl").value(product1.getImageUrl()))
                .andExpect(jsonPath("$.content[0].description").value(product1.getDescription()))
                .andExpect(jsonPath("$.content[0].stockAmount").value(product1.getStockAmount()))
                .andExpect(jsonPath("$.content[1].id").value(product2.getId()))
                .andExpect(jsonPath("$.content[1].name").value(product2.getName()))
                .andExpect(jsonPath("$.content[1].price").value(product2.getPrice()))
                .andExpect(jsonPath("$.content[1].imageUrl").value(product2.getImageUrl()))
                .andExpect(jsonPath("$.content[1].description").value(product2.getDescription()))
                .andExpect(jsonPath("$.content[1].stockAmount").value(product2.getStockAmount()))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void deleteProduct() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(id);
    }

    private ProductDto makeProductDtoFixture(int param) {
        String name = "name " + param;
        int price = param * 1000;
        String url = "url " + param;
        String description = "description " + param;
        int stockAmount = param * 10;

        return new ProductDto(name, price, url, description, stockAmount);
    }

    private Product makeProductFixture(int param) {
        Product product = new Product();
        product.setId((long) param);
        product.setName("name " + param);
        product.setPrice(param * 1000);
        product.setImageUrl("url " + param);
        product.setDescription("description " + param);
        product.setStockAmount(param * 10);
        return product;
    }
}