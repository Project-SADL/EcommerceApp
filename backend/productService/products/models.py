from django.db import models
from categories.models import Category

class Product(models.Model):
    name = models.CharField(max_length=255)
    descripion = models.TextField(blank=True)
    Category = models.ForeignKey(Category, on_delete=models.CASCADE, related_name='products')
    image_url = models.URLField(max_length=500, blank=True)
    is_deleted = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    def __str__(self):
        return self.name