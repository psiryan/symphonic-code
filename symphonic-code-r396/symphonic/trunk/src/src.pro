SOURCES += symphonic.cpp \
           main.cpp
HEADERS += symphonic.h
TEMPLATE = app
CONFIG += warn_on \
	  thread \
          qt
TARGET = ../bin/symphonic
RESOURCES = application.qrc
