from rest_framework import serializers
from .models import Product

class ProductSerializer(serializers.ModelSerializer):
    product_id = serializers.IntegerField(source='id', read_only=True)
    category_id = serializers.IntegerField(source='cateogry.id')
    
    class Meta:
        model = Product
        fields = ['product_id', 'name', 'description', 'category_id', 'image_url', 'created_at', 'updated_at']