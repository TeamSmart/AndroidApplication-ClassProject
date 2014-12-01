from django.contrib.auth.models import User
from django.db.models import Q
from django.shortcuts import get_object_or_404
from django.utils.dateparse import parse_datetime
from hashlib import md5
from messaging.models import Message
from rest_framework import generics
from rest_framework import serializers
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.reverse import reverse
import json

APIS = [('message-list',     []),
        ('messages-to',      ['USER']),
        ('messages-between', ['USER1', 'USER2']),
        ('thread-list',      ['USER']),
        ('login-user',       []),
        ('register-user',    []),
        ]

@api_view(['GET'])
def api_root(request, format=None):
    d = {}
    for key, args in APIS:
        d[key] = reverse(key, request=request, args=args)
    return Response(d)

def email_to_avatar(em):
    if em:
        return 'http://www.gravatar.com/avatar/' + md5(em).hexdigest()
    else:
        return None

def user_avatar(u):
    return email_to_avatar(u.email or u.username)

class UsernameField(serializers.CharField):
    def to_native(self, obj):
        if obj:
            return obj.username
        else:
            return obj

    def from_native(self, name):
        if name:
            return get_object_or_404(User, username=name)
        else:
            return obj

class MessageSerializer(serializers.HyperlinkedModelSerializer):
    sender = UsernameField()
    avatar = serializers.SerializerMethodField('avatar_url')
    recipient = UsernameField()
    timestamp = serializers.DateTimeField(format='iso-8601', required=False)

    def avatar_url(self, mesg):
        return user_avatar(mesg.sender)

    class Meta:
        model = Message
        fields = ('sender', 'avatar', 'recipient', 'timestamp', 'content')

class MessageList(generics.ListCreateAPIView):
    model = Message
    serializer_class = MessageSerializer

def messages(request, ms):
    timestamp = request.GET.get('timestamp')
    if timestamp:
        timestamp = parse_datetime(timestamp)
        ms = ms.filter(timestamp__gt=timestamp)
    return Response(MessageSerializer(ms, many=True).data)

@api_view(['GET'])
def messages_between(request, u1, u2):
    q = (Q(sender__username=u1, recipient__username=u2) |
         Q(sender__username=u2, recipient__username=u1))
    return messages(request, Message.objects.filter(q))

@api_view(['GET'])
def messages_to(request, u):
    return messages(request, Message.objects.filter(recipient__username=u))

@api_view(['GET'])
def thread_list(request, u):
    u = get_object_or_404(User, username=u)
    s = set(u.inbox.values_list('sender__username', 'sender__email').distinct())
    s = s.union(u.outbox.exclude(recipient=None).values_list('recipient__username', 'recipient__email').distinct())
    def mk_thread(user_email_pair):
        u2, e = user_email_pair
        return {'user': u2,
                'avatar': email_to_avatar(e or u2),
                'messages': reverse('messages-between', request=request,
                                    args=[u,u2])}
    return Response(map(mk_thread, s))

def user_pass_data(data):
    d = json.loads(data)
    if (isinstance(d,dict) and 'user' in d and 'password' in d and
        isinstance(d['user'], basestring) and d['user'] and
        isinstance(d['password'], basestring) and d['password']):
        return d['user'], d['password']
    raise ValueError

@api_view(['POST'])
def login_user(request):
    try:
        u, p = user_pass_data(request.body)
        o = User.objects.get(username=u)
        if o.check_password(p):
            return Response(True)
    except User.DoesNotExist:
        pass                    # Handled below
    except ValueError:          # JSON parse failed or result unacceptable
        return Response(False, status=status.HTTP_400_BAD_REQUEST)
    # If user doesn't exist or if password didn't match
    return Response(False, status=status.HTTP_403_FORBIDDEN)

@api_view(['POST'])
def register_user(request):
    try:
        u, p = user_pass_data(request.body)
        o, created = User.objects.get_or_create(username=u)
        if not created:             # Already exists
            return Response(False, status=status.HTTP_409_CONFLICT)
        o.set_password(p)
        o.save()
        return Response(True)
    except ValueError:
        return Response(False, status=status.HTTP_400_BAD_REQUEST)
