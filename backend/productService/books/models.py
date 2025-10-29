from django.db import models
from products.models import Product

class Book(models.Model):
    product = models.OneToOneField(Product, on_delete=models.CASCADE, related_name='book_info')
    author = models.CharField(max_length=100)
    date_published = models.DateField(null=True, blank=True)
    publisher = models.CharField(max_length=100, blank=True, null=True)
    isbn = models.CharField(max_length=13, unique=True)
    pages = models.PositiveBigIntegerField(default=0, null=True, blank=True)
    language = models.CharField(max_length=50, default='English')
    
    def __str__(self):
       return f"{self.product.name} by {self.author}"
    