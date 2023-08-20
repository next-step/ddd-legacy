# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 음식/메뉴/주문을 관리하는 포스기를 구현한다.

### 음식

- [ ] 음식의 가격은 필수이며, 0 이상이어야 한다.
- [ ] 음식의 이름은 필수이며, 비속어가 포함되어 있지 않아야 한다.
- [ ] 음식을 생성할 수 있다.
- [ ] DB에 존재하지 않는 음식에 한해 가격을 수정할 수 있다.
- [ ] 모든 음식을 조회할 수 있다.

#### 음식 생성 API request

```http request
POST /api/products
Content-Type: application/json

{
  "name": "강정치킨",
  "price": 17000
}
```

#### 음식 생성 API response

```http request
POST /api/products
HTTP/1.1 201 

{
  "id": "0d88c45a-6aa7-48e9-a817-21118268cbd8",
  "name": "강정치킨",
  "price": 17000
}
```

#### 음식 가격 수정 API request

```http request
PUT /api/products/3b528244-34f7-406b-bb7e-690912f66b10/price
Content-Type: application/json

{
  "price": 18000
}
```

#### 음식 가격 수정 API response

```http request
PUT /api/products/3b528244-34f7-406b-bb7e-690912f66b10/price
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "3b528244-34f7-406b-bb7e-690912f66b10",
  "name": "후라이드",
  "price": 18000
}
```

#### 음식 전체 조회 API request

```http request
GET /api/products
```

#### 음식 전체 조회 API response

```http request
GET /api/products
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "0ac16db7-1b02-4a87-b9c1-e7d8f226c48d",
    "name": "간장치킨",
    "price": 17000.00
  },
  {
    "id": "0d88c45a-6aa7-48e9-a817-21118268cbd8",
    "name": "강정치킨",
    "price": 17000.00
  }, ...
]
```

### 메뉴

- [ ] 메뉴의 가격은 필수이며, 0 이상이어야 한다. 또한, 메뉴에 포함되는 음식 가격의 합보다 작거나 같아야 한다.
- [ ] 메뉴의 이름은 필수이며, 비속어가 포함되어 있지 않아야 한다.
- [ ] 메뉴는 메뉴 그룹에 속해있어야 한다.
- [ ] 메뉴는 최소한 1개 이상의 음식으로 이루어져야 한다.
- [ ] 메뉴에는 실제로 DB에 존재하는 음식만이 포함되어야 한다.
- [ ] 메뉴에 들어가는 음식의 양은 0 이상이어야 한다.
- [ ] 메뉴를 생성할 수 있다.
- [ ] 메뉴를 화면에 표시할 수 있다.
- [ ] 메뉴를 화면에서 숨길 수 있다.
- [ ] 모든 메뉴를 조회할 수 있다.

#### 메뉴 생성 API request

```http request
POST /api/menus
Content-Type: application/json

{
  "name": "후라이드+후라이드",
  "price": 19000,
  "menuGroupId": "f1860abc-2ea1-411b-bd4a-baa44f0d5580",
  "displayed": true,
  "menuProducts": [
    {
      "productId": "3b528244-34f7-406b-bb7e-690912f66b10",
      "quantity": 2
    }
  ]
}
```

#### 메뉴 생성 API response

```http request
POST /api/menus
HTTP/1.1 201 
Content-Type: application/json

{
  "id": "a1ee1ec1-d992-4205-ba84-6a11416cabd0",
  "name": "후라이드+후라이드",
  "price": 19000,
  "menuGroup": {
    "id": "f1860abc-2ea1-411b-bd4a-baa44f0d5580",
    "name": "두마리메뉴"
  },
  "displayed": true,
  "menuProducts": [
    {
      "seq": 13,
      "product": {
        "id": "3b528244-34f7-406b-bb7e-690912f66b10",
        "name": "후라이드",
        "price": 18000.00
      },
      "quantity": 2,
      "productId": null
    }
  ],
  "menuGroupId": null
}
```

#### 메뉴 가격 수정 API request

```http request
PUT /api/menus/f59b1e1c-b145-440a-aa6f-6095a0e2d63b/price
Content-Type: application/json

{
  "price": 15000
}
```

#### 메뉴 가격 수정 API response

```http request
PUT /api/menus/f59b1e1c-b145-440a-aa6f-6095a0e2d63b/price
HTTP/1.1 200
Content-Type: application/json

{
  "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
  "name": "후라이드치킨",
  "price": 15000,
  "menuGroup": {
    "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
    "name": "한마리메뉴"
  },
  "displayed": true,
  "menuProducts": [
    {
      "seq": 1,
      "product": {
        "id": "3b528244-34f7-406b-bb7e-690912f66b10",
        "name": "후라이드",
        "price": 18000.00
      },
      "quantity": 1,
      "productId": null
    }
  ],
  "menuGroupId": null
}
```

#### 메뉴 화면 표시 API request

```http request
PUT /api/menus/f59b1e1c-b145-440a-aa6f-6095a0e2d63b/display
```

#### 메뉴 화면 표시 API response

```http request
PUT /api/menus/f59b1e1c-b145-440a-aa6f-6095a0e2d63b/display
HTTP/1.1 200
Content-Type: application/json

{
  "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
  "name": "후라이드치킨",
  "price": 15000.00,
  "menuGroup": {
    "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
    "name": "한마리메뉴"
  },
  "displayed": true,
  "menuProducts": [
    {
      "seq": 1,
      "product": {
        "id": "3b528244-34f7-406b-bb7e-690912f66b10",
        "name": "후라이드",
        "price": 18000.00
      },
      "quantity": 1,
      "productId": null
    }
  ],
  "menuGroupId": null
}
```

#### 메뉴 화면 숨김 API request

```http request
PUT /api/menus/f59b1e1c-b145-440a-aa6f-6095a0e2d63b/hide
```

#### 메뉴 화면 숨김 API response

```http request
PUT /api/menus/f59b1e1c-b145-440a-aa6f-6095a0e2d63b/hide
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
  "name": "후라이드치킨",
  "price": 15000.00,
  "menuGroup": {
    "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
    "name": "한마리메뉴"
  },
  "displayed": false,
  "menuProducts": [
    {
      "seq": 1,
      "product": {
        "id": "3b528244-34f7-406b-bb7e-690912f66b10",
        "name": "후라이드",
        "price": 18000.00
      },
      "quantity": 1,
      "productId": null
    }
  ],
  "menuGroupId": null
}
```

#### 메뉴 전체 조회 API request

```http request
GET /api/menus
```

#### 메뉴 전체 조회 API response

```http request
GET /api/menus
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "095719f1-a907-4798-b6d0-0bfe1fde8067",
    "name": "후라이드+후라이드",
    "price": 19000.00,
    "menuGroup": {
      "id": "f1860abc-2ea1-411b-bd4a-baa44f0d5580",
      "name": "두마리메뉴"
    },
    "displayed": true,
    "menuProducts": [
      {
        "seq": 10,
        "product": {
          "id": "3b528244-34f7-406b-bb7e-690912f66b10",
          "name": "후라이드",
          "price": 18000.00
        },
        "quantity": 2,
        "productId": null
      }
    ],
    "menuGroupId": null
  },
  ...
]
```

### 메뉴 그룹

- [ ] 메뉴 그룹의 이름은 필수이다.
- [ ] 모든 메뉴 그룹을 조회할 수 있다.

#### 메뉴 그룹 생성 API request

```http request
POST /api/menu-groups
Content-Type: application/json

{
  "name": "추천메뉴"
}

```

#### 메뉴 그룹 생성 API response

```http request
POST /api/menu-groups
HTTP/1.1 201 
Content-Type: application/json

{
  "id": "5fad78f3-791a-4bc6-be1c-1b9e2561616d",
  "name": "추천메뉴"
}
```

#### 메뉴 그룹 전체 조회 API request

```http request
GET /api/menu-groups

```

#### 메뉴 그룹 전체 조회 API response

```http request
GET /api/menu-groups
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "5e9879b7-6112-4791-a4ce-f22e94af8752",
    "name": "순살파닭두마리메뉴"
  },
  {
    "id": "5fad78f3-791a-4bc6-be1c-1b9e2561616d",
    "name": "추천메뉴"
  },
  ...
]
```

### 주문

- [ ] 주문 타입은 배달 주문(DELEIVERY), 포장 주문(TAKEOUT), 매장에서 식사(EAT_IN)으로 나눠진다.
- [ ] 주문 타입은 필수이다.
- [ ] 주문은 최소한 1개 이상의 음식으로 이루어져야 한다.
- [ ] 주문에는 실제로 DB에 존재하는 메뉴만이 포함되어야 한다.
- [ ] 포장 및 배달 주문의 경우 음식의 양이 0 이상이어야 한다.
- [ ] 화면에 표시된 메뉴만 주문할 수 있다.
- [ ] 메뉴의 현재 가격과 주문시점의 메뉴의 가격이 동일한 경우에만 주문할 수 있다.
- [ ] 배달 주문의 경우 주소지가 필수적으로 있어야 한다.
- [ ] 매장에서 식사하는 경우 먼저 주문 테이블을 잡고 있어야 주문이 가능하다.
- [ ] 주문이 대기중인 경우에만 수락할 수 있다.
- [ ] 배달 주문의 수락은 kitchenridersClient 배달 요청 API를 호출한 후에 이뤄진다.
- [ ] 주문 상태는 대기(WATING), 수락(ACCEPTED), 제공(SERVED), 배달중(DELIVERING), 배달 완료(DELEIVERED), 주문 완료(COMPLETED)로 나눠진다.
- [ ] 주문 상태는 주문 타입과 현재 주문의 상태에 따라 다르게 갱신될 수 있으며, 최종적인 주문 상태는 주문 완료이다.
- [ ] 매장에서 식사하는 경우 주문 완료 상태가 되면 주문 테이블을 비움 처리한다.
- [ ] 모든 주문을 조회할 수 있다.

#### 주문 생성 API request

```http request
POST /api/orders
Content-Type: application/json

{
  "type": "EAT_IN",
  "orderTableId": "8d710043-29b6-420e-8452-233f5a035520",
  "orderLineItems": [
    {
      "menuId": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
      "price": 16000,
      "quantity": 3
    }
  ]
}
```

#### 주문 생성 API response

```http request
POST /api/orders
HTTP/1.1 201 
Content-Type: application/json

{
  "id": "ce410dd9-32e7-4db2-be3c-fe7d8a2855f4",
  "type": "EAT_IN",
  "status": "WAITING",
  "orderDateTime": "2023-08-20T23:11:20.250842",
  "orderLineItems": [
    {
      "seq": 4,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 3,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": null,
  "orderTable": {
    "id": "8d710043-29b6-420e-8452-233f5a035520",
    "name": "1번",
    "numberOfGuests": 4,
    "occupied": true
  },
  "orderTableId": null
}
```

#### 주문 수락 API request

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/accept
```

#### 주문 수락 API response

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/accept
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
  "type": "DELIVERY",
  "status": "ACCEPTED",
  "orderDateTime": "2021-07-27T00:00:00",
  "orderLineItems": [
    {
      "seq": 1,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 1,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": "서울시 송파구 위례성대로 2",
  "orderTable": null,
  "orderTableId": null
}
```

#### 주문 제공 API request

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/serve
```

#### 주문 제공 API response

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/serve
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
  "type": "DELIVERY",
  "status": "SERVED",
  "orderDateTime": "2021-07-27T00:00:00",
  "orderLineItems": [
    {
      "seq": 1,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 1,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": "서울시 송파구 위례성대로 2",
  "orderTable": null,
  "orderTableId": null
}
```

#### 배달 시작 API response

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/start-delivery
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
  "type": "DELIVERY",
  "status": "DELIVERING",
  "orderDateTime": "2021-07-27T00:00:00",
  "orderLineItems": [
    {
      "seq": 1,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 1,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": "서울시 송파구 위례성대로 2",
  "orderTable": null,
  "orderTableId": null
}
```

#### 배달 시작 API request

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/start-delivery
```

#### 배달 시작 API response

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/start-delivery
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
  "type": "DELIVERY",
  "status": "DELIVERING",
  "orderDateTime": "2021-07-27T00:00:00",
  "orderLineItems": [
    {
      "seq": 1,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 1,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": "서울시 송파구 위례성대로 2",
  "orderTable": null,
  "orderTableId": null
}
```

#### 배달 완료 API request

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/complete-delivery
```

#### 배달 완료 API response

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/complete-delivery
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
  "type": "DELIVERY",
  "status": "DELIVERED",
  "orderDateTime": "2021-07-27T00:00:00",
  "orderLineItems": [
    {
      "seq": 1,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 1,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": "서울시 송파구 위례성대로 2",
  "orderTable": null,
  "orderTableId": null
}
```

#### 주문 완료 API request

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/complete

```

#### 주문 완료 API response

```http request
PUT /api/orders/69d78f38-3bff-457c-bb72-26319c985fd8/complete
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
  "type": "DELIVERY",
  "status": "COMPLETED",
  "orderDateTime": "2021-07-27T00:00:00",
  "orderLineItems": [
    {
      "seq": 1,
      "menu": {
        "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
        "name": "후라이드치킨",
        "price": 16000.00,
        "menuGroup": {
          "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
          "name": "한마리메뉴"
        },
        "displayed": true,
        "menuProducts": [
          {
            "seq": 1,
            "product": {
              "id": "3b528244-34f7-406b-bb7e-690912f66b10",
              "name": "후라이드",
              "price": 16000.00
            },
            "quantity": 1,
            "productId": null
          }
        ],
        "menuGroupId": null
      },
      "quantity": 1,
      "menuId": null,
      "price": null
    }
  ],
  "deliveryAddress": "서울시 송파구 위례성대로 2",
  "orderTable": null,
  "orderTableId": null
}
```

#### 주문 전체 조회 API request

```http request
GET /api/orders
```

#### 주문 전체 조회 API response

```http request
GET /api/orders

HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 20 Aug 2023 14:19:07 GMT
Keep-Alive: timeout=60
Connection: keep-alive

[
  {
    "id": "69d78f38-3bff-457c-bb72-26319c985fd8",
    "type": "DELIVERY",
    "status": "COMPLETED",
    "orderDateTime": "2021-07-27T00:00:00",
    "orderLineItems": [
      {
        "seq": 1,
        "menu": {
          "id": "f59b1e1c-b145-440a-aa6f-6095a0e2d63b",
          "name": "후라이드치킨",
          "price": 16000.00,
          "menuGroup": {
            "id": "cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded",
            "name": "한마리메뉴"
          },
          "displayed": true,
          "menuProducts": [
            {
              "seq": 1,
              "product": {
                "id": "3b528244-34f7-406b-bb7e-690912f66b10",
                "name": "후라이드",
                "price": 16000.00
              },
              "quantity": 1,
              "productId": null
            }
          ],
          "menuGroupId": null
        },
        "quantity": 1,
        "menuId": null,
        "price": null
      }
    ],
    "deliveryAddress": "서울시 송파구 위례성대로 2",
    "orderTable": null,
    "orderTableId": null
  },
  ...
]
```

### 주문 테이블

- [ ] 주문 테이블의 이름은 필수이다.
- [ ] 주문 테이블을 생성할 수 있다.
- [ ] 주문 테이블에 손님이 착석하면 착석 처리를 한다.
- [ ] 주문 테이블을 비움 처리할 수 있다.
- [ ] 손님의 수가 0 이상이고, 테이블에 손님이 착석해있는 상태에서만 주문 테이블에 착석한 손님의 숫자를 바꿀 수 있다.
- [ ] 모든 주문 테이블을 조회할 수 있다.

#### 주문 테이블 생성 API request

```http request
POST /api/order-tables
Content-Type: application/json

{
  "name": "9번"
}
```

#### 주문 테이블 생성 API response

```http request
POST /api/order-tables
HTTP/1.1 201 
Content-Type: application/json

{
  "id": "94f1bcf7-aeb9-4ce1-8f5f-f056cf5d1c53",
  "name": "9번",
  "numberOfGuests": 0,
  "occupied": false
}
```

#### 주문 테이블 착석 API request

```http request
PUT /api/order-tables/8d710043-29b6-420e-8452-233f5a035520/sit
```

#### 주문 테이블 착석 API response

```http request
PUT /api/order-tables/8d710043-29b6-420e-8452-233f5a035520/sit
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "8d710043-29b6-420e-8452-233f5a035520",
  "name": "1번",
  "numberOfGuests": 0,
  "occupied": true
}
```

#### 주문 테이블 비움 처리 API request

```http request
PUT /api/order-tables/8d710043-29b6-420e-8452-233f5a035520/clear
```

#### 주문 테이블 비움 처리 API response

```http request
PUT /api/order-tables/8d710043-29b6-420e-8452-233f5a035520/clear
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "8d710043-29b6-420e-8452-233f5a035520",
  "name": "1번",
  "numberOfGuests": 0,
  "occupied": false
}
```

#### 주문 테이블 착석 손님 숫자 변경 API request

```http request
PUT /api/order-tables/8d710043-29b6-420e-8452-233f5a035520/number-of-guests
Content-Type: application/json

{
  "numberOfGuests": 4
}
```

#### 주문 테이블 착석 손님 숫자 변경 API response

```http request
PUT /api/order-tables/8d710043-29b6-420e-8452-233f5a035520/number-of-guests
HTTP/1.1 200 
Content-Type: application/json

{
  "id": "8d710043-29b6-420e-8452-233f5a035520",
  "name": "1번",
  "numberOfGuests": 4,
  "occupied": true
}
```

#### 주문 테이블 전체 조회 API request

```http request
GET /api/order-tables
```

#### 주문 테이블 전체 조회 API response

```http request
GET /api/order-tables
HTTP/1.1 200 
Content-Type: application/json

[
  {
    "id": "3faec3ab-5217-405d-aaa2-804f87697f84",
    "name": "5번",
    "numberOfGuests": 0,
    "occupied": false
  },
  {
    "id": "6ab59e81-06eb-4416-84e9-9faabc87c9ca",
    "name": "2번",
    "numberOfGuests": 0,
    "occupied": false
  },
  ...
]
```

## 용어 사전

| 한글명 | 영문명 | 설명 |
|-----|-----|----|
|     |     |    |

## 모델링
