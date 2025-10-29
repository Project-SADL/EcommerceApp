from django.db import models
from products.models import Product

class GiftCard(models.Model):
    Product = models.OneToOneField(Product, on_delete=models.CASCADE, related_name='giftcard_info')
    code = models.CharField(max_length=50, unique=True)
    balance = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    expiry_date = models.DateField(null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    is_active = models.BooleanField(default=True)
    
    def __str__(self):
        return f"GiftCard {self.code} (${self.balance})"
