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


#ifndef SYMPHONIC_H
#define SYMPHONIC_H

#include <QMainWindow>
#include <QCloseEvent>

class QAction;
class QMenu;

class Symphonic:public QMainWindow
{
      Q_OBJECT

public:
      Symphonic();
      ~Symphonic();

protected:
      void closeEvent(QCloseEvent *event);

private slots:
      void about();

private:
      void createActions();
      void createMenus();
      void createStatusBar();
      void readSettings();
      void writeSettings();

      QMenu *fileMenu;
      QMenu *helpMenu;
      QAction *exitAct;
      QAction *aboutAct;
      QAction *aboutQtAct;

};

#endif
