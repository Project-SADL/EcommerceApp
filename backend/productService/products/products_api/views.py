from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .models import Product
from .serializer import ProductSerializer

@api_view(['GET'])
def get_product(request):
    return Response(ProductSerializer({'name': 'Datuna', 'author': "don Joan",}))
