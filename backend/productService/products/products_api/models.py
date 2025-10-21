from django.db import models
<<<<<<< HEAD
from category_api.models import Category  
=======
>>>>>>> feat/products
 
class Product(models.Model):
    name = models.CharField(max_length=30)
    author = models.CharField(max_length=20)
    date_published = models.DateField()
    category = models.ForeignKey("Category", on_delete=models.CASCADE)
    
    def __str__(self):
        return self.name
    
    

