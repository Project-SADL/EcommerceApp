from django.db import models

# Create your models here.
class Category(models.Model):
    name = models.CharField(max_length=50)
    
class Product(models.Model):
    name = models.CharField(max_length=30)
    author = models.CharField(max_length=20)
    date_published = models.DateField()
    category = models.ForeignKey("Category", on_delete=models.CASCADE)
    
    

