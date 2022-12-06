from django.urls import path
from wv_app import views
urlpatterns = [
    path('learn', views.train.as_view()),
    path('learn/', views.train.as_view()),
    path('question', views.question.as_view()),
    path('question/', views.question.as_view()),
    path('question2', views.question2.as_view()),
    path('question2/', views.question2.as_view()),
] 