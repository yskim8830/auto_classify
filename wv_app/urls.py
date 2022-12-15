from django.urls import path
from wv_app import views
urlpatterns = [
    path('training/start', views.training_start.as_view()),
    path('training/start/', views.training_start.as_view()),
    path('training/status', views.training_status.as_view()),
    path('training/status/', views.training_status.as_view()),
    path('training/stop', views.training_stop.as_view()),
    path('training/stop/', views.training_stop.as_view()),
    path('training/clear', views.training_clear.as_view()),
    path('training/clear/', views.training_clear.as_view()),
    path('dist/service', views.distribute_service.as_view()),
    path('dist/service/', views.distribute_service.as_view()),
    path('dist/dic', views.distribute_dictionary.as_view()),
    path('dist/dic/', views.distribute_dictionary.as_view()),
    path('classify', views.classify.as_view()),
    path('classify/', views.classify.as_view()),
    path('classify2', views.classify2.as_view()),
    path('classify2/', views.classify2.as_view()),
] 