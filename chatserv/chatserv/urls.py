from django.conf.urls import patterns, include, url
from django.contrib import admin
from messaging import views as v

admin.autodiscover()

urlpatterns = patterns(
    '',
    url(r'^ping$', 'ping.views.ping', name='ping'),

    url(r'^api/$', v.api_root, name='api'),
    url(r'^api/messages/$', v.MessageList.as_view(), name='message-list'),
    url(r'^api/messages/(?P<u>[^/]+)/$',
        v.messages_to, name='messages-to'),
    url(r'^api/messages/(?P<u1>[^/]+)/(?P<u2>[^/]+)/$',
        v.messages_between, name='messages-between'),
    url(r'^api/threads/(?P<u>[^/]+)/$',
        v.thread_list, name='thread-list'),
    url(r'^api/login/$',
        v.login_user, name='login-user'),
    url(r'^api/register/$',
        v.register_user, name='register-user'),

    # Uncomment the admin/doc line below to enable admin documentation:
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
)
