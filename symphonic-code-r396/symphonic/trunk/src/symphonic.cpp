/***************************************************************************
 *   Copyright (C) 2007 by Sylvain Paré   *
 *   garthps@users.sourceforge.net   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/


#include <QtGui>
#include "symphonic.h"

#include <QCloseEvent>
#include <QFileDialog>

Symphonic::Symphonic()
{
	QLabel *showicon=new QLabel();
	showicon->setPixmap ( QPixmap ( "/NWA1200.xpm" ) );
	this->setCentralWidget ( showicon );

	createActions();
	createMenus();
	createStatusBar();

	readSettings();
}

void Symphonic::closeEvent ( QCloseEvent *event )
{
	writeSettings();
	event->accept();
}

void Symphonic::about()
{
	QMessageBox::about ( this, tr ( "About Symphonic" ),
	                     tr ( "<b>Symphonic</b> is a graphical interface that provides you all "
	                          "that you had always wanted from Sony to manage your music on your New Sony Walkman" ) );
}

void Symphonic::createActions()
{
	exitAct = new QAction ( tr ( "E&xit" ), this );
	exitAct->setShortcut ( tr ( "Ctrl+Q" ) );
	exitAct->setStatusTip ( tr ( "Exit the application" ) );
	connect ( exitAct, SIGNAL ( triggered() ), this, SLOT ( close() ) );

	aboutAct = new QAction ( tr ( "&About" ), this );
	aboutAct->setStatusTip ( tr ( "Show the application's About box" ) );
	connect ( aboutAct, SIGNAL ( triggered() ), this, SLOT ( about() ) );

	aboutQtAct = new QAction ( tr ( "About &Qt" ), this );
	aboutQtAct->setStatusTip ( tr ( "Show the Qt library's About box" ) );
	connect ( aboutQtAct, SIGNAL ( triggered() ), qApp, SLOT ( aboutQt() ) );
}

void Symphonic::createMenus()
{
	fileMenu = menuBar()->addMenu ( tr ( "&File" ) );
	fileMenu->addSeparator();
	fileMenu->addAction ( exitAct );

	menuBar()->addSeparator();

	helpMenu = menuBar()->addMenu ( tr ( "&Help" ) );
	helpMenu->addAction ( aboutAct );
	helpMenu->addAction ( aboutQtAct );
}

void Symphonic::createStatusBar()
{
	statusBar()->showMessage ( tr ( "Ready" ) );
}

void Symphonic::readSettings()
{
	QSettings settings ( "SymphonicTeam", "Symphonic" );
	QPoint pos = settings.value ( "pos", QPoint ( 200, 200 ) ).toPoint();
	QSize size = settings.value ( "size", QSize ( 400, 400 ) ).toSize();
	resize ( size );
	move ( pos );
}

void Symphonic::writeSettings()
{
	QSettings settings ( "SymphonicTeam", "Symphonic" );
	settings.setValue ( "pos", pos() );
	settings.setValue ( "size", size() );
}

Symphonic::~Symphonic()
{

}

