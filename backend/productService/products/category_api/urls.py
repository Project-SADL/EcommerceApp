from django.urls import path
from .views import get_category

urlpatterns = [
    path('category/', get_category, name ='get_category')
]
