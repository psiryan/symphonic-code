// CommonDefines.h
/***************************************************************************
 *   Copyright (C) 2007 by Sylvain Paré                                    *
 *   garthps@users.sourceforge.net                                         *
 *                                                                         *
 *   The original work was from OtiasJ from the project ml_sony under BSD  *
 *   licence. Huge thanks to him ;)                                        *
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

#if !defined(COMMON_DEFINES_H)
#define COMMON_DEFINES_H

#define libsonynw_version "1.1.0"

#include <QDir>
#include <QFileInfo>
#include <QFileDialog>
#include <QString>
#include <QWidget>
#include <QObject>

#ifndef __Win32__
# include <stdio.h>
# include <stdint.h>
# include <unistd.h>
# include <wchar.h>

# define __int64 int64_t

# define GetLogicalDrives()			 \
{						 \
	system("mount | grep /dev/sd")		 \  
}//not very usefull..

# define wsprintf(output, args...)               \
{                                                \
	char buffer[512], * p=buffer;            \
	sprintf(buffer, args);                   \
	for(int i=0; * p; i++, p++)              \
	{                                        \
		output[i * 2]=0;                 \
		output[i * 2 + 1]=buffer[i];     \
	}                                        \
}
# define _strnicmp strncasecmp
# define _stricmp strcasecmp
# define DeleteFile(f) unlink(f)
#endif

#ifdef __Win32__
# include <windows.h>
# include <direct.h>
# include <usb.h>
#endif


//log file

#define DEBUG 1

#ifdef DEBUG
static FILE *debugFile1 = fopen ( "c:\\log1.txt", "w" );
static FILE *debugFile2 = fopen ( "c:\\log2.txt", "w" );
#define LOGFUNCCALL(x)      fprintf(debugFile1, x );fflush(debugFile1);
#define LOG(x)              fprintf(debugFile2, x );fflush(debugFile2);
#else
#define LOGFUNCCALL(x)
#define LOG(x)
#endif

typedef enum
{
	SUCCESS,
	FAILED,
	INVALID_PARAMETER,
	INVALID_PARAMETER_1,
	INVALID_PARAMETER_2,
	INVALID_PARAMETER_3,
	INVALID_PARAMETER_4,
	INVALID_PARAMETER_5
} ResultCode;

#define DELIMITER_CHAR		"\\\\"
#define SIZE_DELIMITER_CHAR 2

typedef short 				int16;
typedef int 				int32;
typedef char 				int8;
typedef unsigned short 	    uint16;
typedef unsigned int 	    uint32;
typedef unsigned char 	    uint8;
typedef unsigned short      utf16char;
typedef __int64             largeInteger;


#endif /* !COMMON_DEFINES_H */
