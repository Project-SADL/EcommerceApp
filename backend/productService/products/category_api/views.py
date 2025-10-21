from django.shortcuts import render
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .models import Category
from .seralizer import CategorySerializer


@api_view(['GET'])
def get_category(request):
    return Response(CategorySerializer({'name': 'fiction', 'description': 'Fiction is awesome'}).data)