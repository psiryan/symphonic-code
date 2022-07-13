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

#include <string>
#include <iostream>
#include <sstream>
#include "Song.h"
#include "SonyDb.h"
#include "CommonDefines.h"


using namespace std;
using namespace SonyDb;


Song::Song()
{
	fileName_ = new string ( "UNKNOWN" );
	artist_ = new string ( "UNKNOWN" );
	album_ = new string ( "UNKNOWN" );
	title_ = new string ( "UNKNOWN" );
	genre_ = new string ( "UNKNOWN" );
	track_ = -1;
	year_ = -1;
	length_ = 0;
	progress_ = 0;
	status_ = UNKNOWN;
}

Song::~Song()
{
	if ( fileName_ != NULL ) delete fileName_;
	if ( artist_ != NULL ) delete artist_;
	if ( album_ != NULL ) delete album_;
	if ( title_ != NULL ) delete title_;
	if ( genre_ != NULL ) delete genre_;
}

bool Song::setFileName ( const string fileName )
{
	if ( fileName.size() <= 0 )
		return false;
	if ( fileName.find ( DELIMITER_CHAR ) != string::npos )
		return false;

	if ( fileName_ != NULL ) delete fileName_;

	fileName_ = new string ( fileName.c_str() );

	return true;
}


bool Song::setFileName ( const char *fileName )
{
	if ( fileName == NULL )
		return false;

	string *tmp = new string ( fileName );
	if ( tmp->find ( DELIMITER_CHAR ) != string::npos )
	{
		delete tmp;
		return false;
	}

	if ( fileName_ != NULL ) delete fileName_;

	fileName_ = tmp;

	return true;
}

bool Song::setArtist ( const string artist )
{
	if ( artist.size() <= 0 )
		return false;

	if ( artist.find ( DELIMITER_CHAR ) != string::npos )
		return false;

	if ( artist_ != NULL ) delete artist_;

	artist_ = new string ( artist.c_str() );

	return true;
}

bool Song::setArtist ( const char *artist )
{
	if ( artist == NULL )
		return false;

	string *tmp = new string ( artist );
	if ( tmp->find ( DELIMITER_CHAR ) != string::npos )
	{
		delete tmp;
		return false;
	}

	if ( artist_ != NULL ) delete artist_;

	artist_ = tmp;

	return true;
}

bool Song::setAlbum ( const string album )
{
	if ( album.size() <= 0 )
		return false;

	if ( album.find ( DELIMITER_CHAR ) != string::npos )
		return false;

	if ( album_ != NULL ) delete album_;

	album_ = new string ( album.c_str() );

	return true;
}

bool Song::setAlbum ( const char *album )
{
	if ( album == NULL )
		return false;

	string *tmp = new string ( album );
	if ( tmp->find ( DELIMITER_CHAR ) != string::npos )
	{
		delete tmp;
		return false;
	}

	if ( album_ != NULL ) delete album_;

	album_ = tmp;

	return true;
}

bool Song::setTitle ( const string title )
{
	if ( title.size() <= 0 )
		return false;

	if ( title.find ( DELIMITER_CHAR ) != string::npos )
		return false;

	if ( title_ != NULL ) delete title_;

	title_ = new string ( title.c_str() );

	return true;
}

bool Song::setTitle ( const char *title )
{
	if ( title == NULL )
		return false;

	string *tmp = new string ( title );
	if ( tmp->find ( DELIMITER_CHAR ) != string::npos )
	{
		delete tmp;
		return false;
	}

	if ( title_ != NULL ) delete title_;

	title_ = tmp;

	return true;
}

bool Song::setGenre ( const string genre )
{
	if ( genre.size() <= 0 )
		return false;

	if ( genre.find ( DELIMITER_CHAR ) != string::npos )
		return false;

	if ( genre_ != NULL ) delete genre_;

	genre_ = new string ( genre.c_str() );

	return true;
}

bool Song::setGenre ( const char *genre )
{
	if ( genre == NULL )
		return false;

	string *tmp = new string ( genre );
	if ( tmp->find ( DELIMITER_CHAR ) != string::npos )
	{
		delete tmp;
		return false;
	}

	if ( genre_ != NULL ) delete genre_;

	genre_ = tmp;

	return true;
}


bool Song::setTrack ( int track )
{
	track_ = track;
	return true;
}

bool Song::setYear ( int year )
{
	year_ = year;
	return true;
}

//length of the song in seconds
bool Song::setLength ( int length )
{
	length_ = length;
	return true;
}


// mpeg version(2bits), layer version(2bits), bitrate(4bits)
bool Song::setEncoding ( int encoding )
{
	encoding_ = encoding;
	return true;
}

// Status of the song
// Song::PRESENT 0 is present on player
// Song::ADD_TO_DEVICE 1 is not present on player needs & to be added
// Song::DEL_FR_DEVICE 2 is present & needs to be removed
// Song::EMPTYTRACK
bool Song::setStatus ( int status )
{
	status_ = status;
	return true;
}

// percent of copy realized
bool Song::setProgress ( int newPercent )
{
	progress_ = newPercent;
	return true;
}


bool Song::setDbIndex ( int index )
{
	if ( index <= 0 )
		return false;

	sonyDbOrder_ = index;
	return true;
}


string Song::getFileName()
{
	return *fileName_;
}

string Song::getArtist()
{
	return *artist_;
}

string Song::getAlbum()
{
	return *album_;
}

string Song::getTitle()
{
	return *title_;
}

string Song::getGenre()
{
	return *genre_;
}

int Song::getTrack()
{
	return track_;
}

int Song::getYear()
{
	return year_;
}

int Song::getLength()
{
	return length_;
}

int Song::getStatus()
{
	return status_;
}

int Song::getProgress()
{
	return progress_;
}

int Song::getEncoding()
{
	return encoding_;
}

int Song::getDbIndex()
{
	return sonyDbOrder_;
}


bool Song::isSameArtist ( Song* song )
{
	string s = song->getArtist();
	if ( s.size() <= 0 )
		return false;

	if ( *this->artist_ == s )
		return true;

	return false;
}

bool Song::isSameAlbum ( Song* song )
{
	string s = song->getAlbum();
	if ( s.size() <= 0 )
		return false;

	if ( *this->album_ == s )
		return true;

	return false;
}

bool Song::isSameGenre ( Song* song )
{
	string s = song->getGenre();
	if ( s.size() <= 0 )
		return false;

	if ( *this->genre_ == s )
		return true;

	return false;
}

bool Song::isSameTitle ( Song* song )
{
	string s = song->getTitle();
	if ( s.size() <= 0 )
		return false;

	if ( *this->title_ == s )
		return true;

	return false;
}

string *Song::asString()
{
	ostringstream oss;

	oss <<  "Song" << DELIMITER_CHAR << "Index=" << getDbIndex() << DELIMITER_CHAR
	<< "FileName=" << getFileName() << DELIMITER_CHAR
	<< "Artist=" << getArtist() << DELIMITER_CHAR
	<< "Album=" << getAlbum() << DELIMITER_CHAR
	<< "Title=" << getTitle() << DELIMITER_CHAR
	<< "Genre=" << getGenre() << DELIMITER_CHAR
	<< "Track=" << getTrack() << DELIMITER_CHAR
	<< "Year=" << getYear() << DELIMITER_CHAR
	<< "Length=" << getLength() << DELIMITER_CHAR
	<< "Encoding=" << getEncoding() << DELIMITER_CHAR << flush;

	string *res = new string ( oss.str() );
	return res;
}

//create a song object from a correct string
bool Song::songFromString ( string songDescription, Song *s )
{
	size_t index;
	string selection;

	//get Descriptor
	index = songDescription.find ( DELIMITER_CHAR );
	if ( index == string::npos )
		return false;
	selection = songDescription.substr ( 0, index );
	songDescription = songDescription.substr ( index + SIZE_DELIMITER_CHAR );
	if ( selection != "Song" )
		return false;

	int i = 0;

	i = atoi ( Helper::getNextValue ( &songDescription ).c_str() );
	s->setDbIndex ( i );

	s->setFileName ( Helper::getNextValue ( &songDescription ) );
	s->setArtist ( Helper::getNextValue ( &songDescription ) );
	s->setAlbum ( Helper::getNextValue ( &songDescription ) );
	s->setTitle ( Helper::getNextValue ( &songDescription ) );
	s->setGenre ( Helper::getNextValue ( &songDescription ) );

	i = atoi ( Helper::getNextValue ( &songDescription ).c_str() );
	s->setTrack ( i );

	i = atoi ( Helper::getNextValue ( &songDescription ).c_str() );
	s->setYear ( i );

	i = atoi ( Helper::getNextValue ( &songDescription ).c_str() );
	s->setLength ( i );

	i = atoi ( Helper::getNextValue ( &songDescription ).c_str() );
	s->setEncoding ( i );

	return true;
}



