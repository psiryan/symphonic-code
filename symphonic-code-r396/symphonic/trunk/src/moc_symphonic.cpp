/****************************************************************************
** Meta object code from reading C++ file 'symphonic.h'
**
** Created: Thu Jan 24 02:20:36 2008
**      by: The Qt Meta Object Compiler version 59 (Qt 4.3.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "symphonic.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'symphonic.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 59
#error "This file was generated using the moc from 4.3.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

static const uint qt_meta_data_Symphonic[] = {

 // content:
       1,       // revision
       0,       // classname
       0,    0, // classinfo
       1,   10, // methods
       0,    0, // properties
       0,    0, // enums/sets

 // slots: signature, parameters, type, tag, flags
      11,   10,   10,   10, 0x08,

       0        // eod
};

static const char qt_meta_stringdata_Symphonic[] = {
    "Symphonic\0\0about()\0"
};

const QMetaObject Symphonic::staticMetaObject = {
    { &QMainWindow::staticMetaObject, qt_meta_stringdata_Symphonic,
      qt_meta_data_Symphonic, 0 }
};

const QMetaObject *Symphonic::metaObject() const
{
    return &staticMetaObject;
}

void *Symphonic::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_Symphonic))
	return static_cast<void*>(const_cast< Symphonic*>(this));
    return QMainWindow::qt_metacast(_clname);
}

int Symphonic::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QMainWindow::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: about(); break;
        }
        _id -= 1;
    }
    return _id;
}
