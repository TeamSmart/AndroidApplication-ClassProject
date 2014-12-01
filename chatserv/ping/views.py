# Create your views here.

from django.http import HttpResponse

def ping(request):
    from django.contrib.auth import get_user_model
    get_user_model().objects.all()[:5]
    return HttpResponse('Pong!')
