export const selectJson = {
  '@column': [
    'orders.id AS order_id',
    'users.username',
    'products.name AS product_name',
    'products.price',
    'orders.quantity',
    '(products.price * orders.quantity) AS total'
  ],
  '@where': [
    {
      column: 'users.username',
      condition: 'EQ',
      value: 'alice'
    }
  ],
  '@join': [
    {
      type: 'INNER_JOIN',
      joinTable: 'users',
      on: 'orders.user_id = users.id'
    },
    {
      type: 'INNER_JOIN',
      joinTable: 'products',
      on: 'orders.product_id = products.id'
    }
  ],
  '@order': ['orders.order_date'],
  '@group': null,
  '@distince': false
}; /**/

export const selectPageJson = {
  '@column': [
    'orders.id AS order_id',
    'users.username',
    'products.name AS product_name',
    'products.price',
    'orders.quantity',
    '(products.price * orders.quantity) AS total'
  ],
  '@where': [
    {
      column: 'users.username',
      condition: 'EQ',
      value: 'alice'
    }
  ],
  '@page': {
    pageIndex: 0,
    pageSize: 10
  },
  '@join': [
    {
      type: 'INNER_JOIN',
      joinTable: 'users',
      on: 'orders.user_id = users.id'
    },
    {
      type: 'INNER_JOIN',
      joinTable: 'products',
      on: 'orders.product_id = products.id'
    }
  ],
  '@order': ['orders.order_date'],
  '@distince': false
};

export const insertJson = {
  '@set': {
    'id': '26a05ba3-913d-4085-a505-36d40021c8d1',
    'username': 'wb04307201',
    'email': 'wb04307201@gitee.com',
    'password': '123456'
  },
  '@with_select': {
    '@column': null,
    '@where': [
      {
        column: 'id',
        condition: 'EQ',
        value: '26a05ba3-913d-4085-a505-36d40021c8d1'
      }
    ],
    '@join': null,
    '@order': null,
    '@group': null,
    '@distince': false
  }
};

export const updateJson = {
  '@set': {
    email: 'wb04307201@github.com'
  },
  '@where': [
    {
      column: 'id',
      condition: 'EQ',
      value: '26a05ba3-913d-4085-a505-36d40021c8d1'
    }
  ],
  '@with_select': {
    '@column': null,
    '@where': [
      {
        column: 'id',
        condition: 'EQ',
        value: '26a05ba3-913d-4085-a505-36d40021c8d1'
      }
    ],
    '@join': null,
    '@order': null,
    '@group': null,
    '@distince': false
  }
};

export const deleteJson = {
  '@where': [
    {
      column: 'id',
      condition: 'EQ',
      value: '26a05ba3-913d-4085-a505-36d40021c8d1'
    }
  ],
  '@with_select': {
    '@column': null,
    '@where': [
      {
        column: 'id',
        condition: 'EQ',
        value: '26a05ba3-913d-4085-a505-36d40021c8d1'
      }
    ],
    '@join': null,
    '@order': null,
    '@group': null,
    '@distince': false
  }
};
