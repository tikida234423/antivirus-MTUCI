package ru.mtuci.demo.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.function.array.AbstractArrayTrimFunction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.Product;
import ru.mtuci.demo.model.request.ProductCreateRequest;
import ru.mtuci.demo.model.request.ProductUpdateRequest;
import ru.mtuci.demo.model.response.GetAllProductsResponse;
import ru.mtuci.demo.model.response.ProductCreateResponse;
import ru.mtuci.demo.model.response.ProductUpdateResponse;
import ru.mtuci.demo.service.LicenseService;
import ru.mtuci.demo.service.ProductService;

import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final LicenseService licenseService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> getAllProducts(@RequestParam(value = "id", required = false) Long id) {

        GetAllProductsResponse response = new GetAllProductsResponse();

        try {
            if (id != null) {
                Product product = productService.getProductById(id);

                if (product == null) {

                    response.setStatus("Product not found");

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(response);
                }

                response.setProducts(new ArrayList<>());
                response.getProducts().add(product);
                response.setStatus("Ok");

                return ResponseEntity.ok(response);
            }

            response.setProducts(productService.getAllProducts());
            response.setStatus("Ok");

            return ResponseEntity.ok(response);
        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request) {

        ProductCreateResponse response = new ProductCreateResponse();

        try {

            Long id = productService.createProduct(request.getName(),
                                                    request.getIsBlocked());

            response.setId(id);
            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('modification')")
    public ResponseEntity<?> updateProduct(@RequestBody ProductUpdateRequest request) {

        ProductUpdateResponse response = new ProductUpdateResponse();

        try {

            Boolean result = productService.updateProduct(request.getProductId(),
                                                        request.getName(),
                                                        request.getIsBlocked());

            if (!result) {

                response.setStatus("Product not found");

                return ResponseEntity.badRequest()
                        .body(response);

            }

            response.setStatus("Ok");

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            response.setStatus(e.getMessage());

            return ResponseEntity.internalServerError()
                    .body(response);

        }

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('modification')")
    @Transactional
    public ResponseEntity<?> deleteProduct(@RequestParam(value = "id", required = false) Long id) {
        try {

            Product deletedProduct = productService.getProductById(id);

            if (deletedProduct == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Product not found");
            }

            License license = licenseService.getLicenseByProductId(id);

            if (license == null)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("License not found");
            }

            productService.deleteProductById(deletedProduct.getId());

            return ResponseEntity.ok().body("Product deleted successfully");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
