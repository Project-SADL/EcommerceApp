from django.db import models
from products.models import Product

class ProductVariant(models.Model):
    product= models.ForeignKey(Product, on_delete= models.CASCADE, related_name='variants')
    sku = models.CharField(max_length=100, unique=True)
    delivery_method = models.CharField(
        max_length=50,
        choices= [
            ('digital', 'Digital'),
            ('physical', 'Physical'),
        ],
        default= 'physical'
    )
    price = models.DecimalField(max_digits=10, decimal_places=2)
    stock = models.PositiveIntegerField(default=0)
    
    def __str__(self):
        return f"{self.product.name} - {self.sku} "
