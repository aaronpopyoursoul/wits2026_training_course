

const subtotal = items.value.reduce(
  (sum, item) => sum + item.product.price * item.quantity,
  0
)