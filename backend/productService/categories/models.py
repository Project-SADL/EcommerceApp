from django.db import models

class Category(models.Model):
    name = models.CharField(max_length=100)
    image_url = models.URLField(max_length=500, blank=True, null=True)
    parent_category = models.ForeignKey(
        'self',
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='subcategories'
    )
    is_active = models.BooleanField(default=True)
    slug = models.SlugField(unique=True, blank=True, null=True)

    def __str__(self):
        return self.name