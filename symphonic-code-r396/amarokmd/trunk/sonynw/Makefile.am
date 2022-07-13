kde_module_LTLIBRARIES = libamarok_sonynw-mediadevice.la
kde_services_DATA = amarok_sonynw-mediadevice.desktop

INCLUDES = \
    -I$(top_srcdir)/amarok/src \
    -I$(top_srcdir)/amarok/src/amarokcore \
    -I$(top_builddir)/amarok/src/amarokcore \
    -I$(top_srcdir)/amarok/src/engine \
    -I$(top_srcdir)/amarok/src/engine \
    -I$(top_srcdir)/amarok/src/mediadevice \
    $(taglib_includes) \
    $(all_includes)

METASOURCES = AUTO

libamarok_sonynw_mediadevice_la_LIBADD  = \
    $(top_builddir)/amarok/src/libamarok.la \
    -lkdeui -lkdecore -lkio

libamarok_sonynw_mediadevice_la_LDFLAGS = \
    $(KDE_PLUGIN) \
    $(all_libraries)

libamarok_sonynw_mediadevice_la_SOURCES = \
    sonynwmediadevice.cpp

noinst_HEADERS = \
    sonynwmediadevice.h

