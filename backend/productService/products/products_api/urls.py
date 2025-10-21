from django.urls import path
from .views import get_product


urlpatterns = [
    path('product/', get_product, name='get_product')
]
