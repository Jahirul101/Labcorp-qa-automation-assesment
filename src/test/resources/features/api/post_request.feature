Feature: API POST Request Validation

  @API @Smoke
  Scenario: Validate POST request with order data
    When I send a POST request to "/sample-request"
    And I include query parameter "author" with value "beeceptor"
    And I send the following order payload:
      """
      {
        "order_id": "12345",
        "customer": {
          "name": "Jane Smith",
          "email": "janesmith@example.com",
          "phone": "1-987-654-3210",
          "address": {
            "street": "456 Oak Street",
            "city": "Metropolis",
            "state": "NY",
            "zipcode": "10001",
            "country": "USA"
          }
        },
        "items": [
          {
            "product_id": "A101",
            "name": "Wireless Headphones",
            "quantity": 1,
            "price": 79.99
          },
          {
            "product_id": "B202",
            "name": "Smartphone Case",
            "quantity": 2,
            "price": 15.99
          }
        ],
        "payment": {
          "method": "credit_card",
          "transaction_id": "txn_67890",
          "amount": 111.97,
          "currency": "USD"
        },
        "shipping": {
          "method": "standard",
          "cost": 5.99,
          "estimated_delivery": "2024-11-15"
        },
        "order_status": "processing",
        "created_at": "2024-11-07T12:00:00Z"
      }
      """
    Then the response status code should be 200 or 201
    And the customer name should be "Jane Smith"
    And the customer email should be "janesmith@example.com"
    And the payment method should be "credit_card"
    And the payment amount should be 111.97
    And the product items should contain "Wireless Headphones" with quantity 1
    And the product items should contain "Smartphone Case" with quantity 2
    And the response should include the "order_id" with value "12345"
    And the response should include the "order_status" with value "processing"
