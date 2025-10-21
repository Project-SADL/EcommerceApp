from django.db import models 
 
class Product(models.Model):
    name = models.CharField(max_length=30)
    author = models.CharField(max_length=20)
    date_published = models.DateField(auto_now_add=True)
    category = models.ForeignKey("category_api.Category", on_delete=models.CASCADE)
    
    def __str__(self):
        return self.name
    
    

