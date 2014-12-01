#!/usr/bin/env python
from django.contrib.auth.models import User
u, created = User.objects.get_or_create(username='admin')
if created:
    from uuid import uuid4
    p = uuid4().hex[:10]
    print "Initial admin password:", p
    u.set_password(p)
    u.is_superuser = True
    u.is_staff = True
    u.save()
