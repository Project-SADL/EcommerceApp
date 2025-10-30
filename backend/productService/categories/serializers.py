from rest_framework import serializers
from .models import Category

class CategorySerializer(serializers.ModelSerializer):
    category_id = serializers.IntegerField(source='id', read_only=True)
    parent_category_id = serializers.IntegerField(source='parent_category.id', allow_null=True)
     
    
    class Meta:
        model = Category
        fields = ['category_id', 'name', 'image_url', 'parent_category_id', 'is_active', 'slug']
