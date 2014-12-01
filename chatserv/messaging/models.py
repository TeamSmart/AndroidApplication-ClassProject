# Create your models here.       -*- coding: utf-8 -*-

from django.contrib import admin
from django.contrib.auth.models import User
from django.db import models
from django.utils import timezone

class ChatRoom(models.Model):
    owner = models.ForeignKey(User, related_name='my_rooms')
    name = models.CharField(max_length=128)
    created_at = models.DateTimeField(editable=False, default=timezone.now)
    participants = models.ManyToManyField(User, blank=True)

    class Meta:
        ordering = ['owner', 'name']

    def __unicode__(self):
        return '%s(%s)' % (self.name, self.owner)

class Message(models.Model):
    sender = models.ForeignKey(User, related_name='outbox')
    recipient = models.ForeignKey(User, blank=True, null=True, related_name='inbox')
    room = models.ForeignKey(ChatRoom, blank=True, null=True)
    timestamp = models.DateTimeField(editable=False, default=timezone.now)
    content = models.TextField()

    class Meta:
        ordering = ['timestamp']

    def destination(self):
        return self.recipient or self.room

    def __unicode__(self):
        return u'%s • %s ⇒ %s' % (self.timestamp, self.sender, self.destination())

admin.site.register(ChatRoom)
admin.site.register(Message)

