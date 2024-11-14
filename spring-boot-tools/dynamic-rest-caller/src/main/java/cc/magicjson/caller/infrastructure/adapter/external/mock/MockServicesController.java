package cc.magicjson.caller.infrastructure.adapter.external.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 *
 * 模拟服务控制器 其中注释示例为动态Rest服务调用案例
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@RestController
public class MockServicesController {

    /**
     * 获取用户信息
     * 示例请求体:
     *  {
     *     "serviceName": "userService",
     *     "endpointName": "getUserInfo",
     *     "payload": {
     *         "userId": "12345"
     *     },
     *     "uriVariables": {
     *         "id": "12345"
     *     }
     *  }
     * @param id 用户ID
     * @return 包含用户信息的ResponseEntity
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable String id) {
        User user = new User(id, "John Doe", "john.doe@example.com");
        return ResponseEntity.ok(user);
    }

    /**
     * 创建新用户
     * 示例请求体:
     *  {
     *     "serviceName": "userService",
     *     "endpointName": "createUser",
     *     "payload": {
     *         "name": "John Doe",
     *         "email": "EMAIL"
     *     },
     *     "uriVariables": {
     *         "id": "12345"
     *     }
     *  }
     * @param user 要创建的用户信息
     * @return 包含创建的用户信息的ResponseEntity
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(new User(user.name, user.email));
    }

    /**
     * 创建新订单
     * 示例请求体:
     *  {
     *     "serviceName": "orderService",
     *     "endpointName": "createOrder",
     *     "payload": {
     *         "customerId": "67890",
     *         "items": [
     *             {
     *                 "productId": "P001",
     *                 "quantity": 2
     *             },
     *             {
     *                 "productId": "P002",
     *                 "quantity": 1
     *             }
     *         ],
     *         "shippingAddress": {
     *             "street": "123 Main St",
     *             "city": "Anytown",
     *             "country": "USA",
     *             "zipCode": "12345"
     *         }
     *     },
     *     "uriVariables": {}
     *  }
     * @param order 要创建的订单信息
     * @return 包含创建的订单信息的ResponseEntity
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(
            new Order(order.customerId, order.items, order.shippingAddress));
    }

    /**
     * 获取订单详情
     * 示例请求体:
     *  {
     *     "serviceName": "orderService",
     *     "endpointName": "getOrderDetails",
     *     "payload": {},
     *     "uriVariables": {
     *         "id": "12345"
     *     }
     *  }
     * @param id 订单ID
     * @return 包含订单详情的ResponseEntity
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable String id) {
        Order order = new Order(id, "67890", List.of(
            new OrderItem("P001", 2),
            new OrderItem("P002", 1)
        ), new Address("123 Main St", "Anytown", "USA", "12345"));
        return ResponseEntity.ok(order);
    }

    /**
     * 搜索产品
     * 示例请求体:
     *  {
     *     "serviceName": "productService",
     *     "endpointName": "searchProducts",
     *     "payload": {},
     *     "uriVariables": {
     *         "category": "electronics",
     *         "minPrice": "100",
     *         "maxPrice": "1000"
     *     }
     *  }
     * @param category 产品类别
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 包含符合条件的产品列表的ResponseEntity
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(
        @RequestParam String category,
        @RequestParam double minPrice,
        @RequestParam double maxPrice) {
        List<Product> products = List.of(
            new Product("P001", "Smartphone", "electronics", 599.99),
            new Product("P002", "Laptop", "electronics", 999.99),
            new Product("P003", "Headphones", "electronics", 199.99)
        );
        List<Product> filteredProducts = products.stream()
            .filter(p -> p.category.equals(category))
            .filter(p -> p.price >= minPrice && p.price <= maxPrice)
            .toList();
        return ResponseEntity.ok(filteredProducts);
    }

    /**
     * 获取产品详情
     * 示例请求体:
     *  {
     *     "serviceName": "productService",
     *     "endpointName": "getProductDetails",
     *     "payload": {},
     *     "uriVariables": {
     *         "id": "P001"
     *     }
     *  }
     * @param id 产品ID
     * @return 包含产品详情的ResponseEntity
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductDetails(@PathVariable String id) {
        Product product = new Product(id, "Sample Product", "electronics", 299.99);
        return ResponseEntity.ok(product);
    }


    record User(String id, String name, String email){
        User(String name, String email) {
            this(UUID.randomUUID().toString(), name, email);
        }
    }

    record Order(String id, String customerId, List<OrderItem> items
        , Address shippingAddress){
        Order(String customerId, List<OrderItem> items, Address shippingAddress){
            this(UUID.randomUUID().toString(), customerId, items, shippingAddress);
        }
    }

    record OrderItem(String productId, double quantity) {
        OrderItem(double quantity) {
            this(UUID.randomUUID().toString(), quantity);
        }
    }

    record Address(String street, String city, String country, String zipCode) {
    }

    record Product(String id, String name, String category, double price) {
        Product( String name, String category, double price) {
            this(UUID.randomUUID().toString(), name, category, price);
        }
    }
}
